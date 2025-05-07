package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.anxiety.AnxietyDatabaseConfiguration
import io.github.coden256.database.transaction
import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.apache.commons.lang3.Range
import org.apache.commons.text.similarity.CosineSimilarity
import org.apache.commons.text.similarity.JaccardSimilarity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.*
import kotlin.time.toKotlinDuration

@Import(AnxietyDatabaseConfiguration::class)
@Component
class AnxietyJournalVerifier(
    private val anxietyDatabase: Database
): Verifier<AnxietyJournalVerifier.Config>() {

    data class Config(val minWords: Long,
                      val cosineThreshold: Double,
                      val jaccardThreshold: Double,
                      val expiry: Duration,
                      val timeRange: Range<LocalTime>,
                      val daysOfWeek: List<DayOfWeek>,
                      val negate: Boolean = false
    ): VerifierConfig

    val verifier by lazy {
        ScheduleVerifier()
            .apply { config = ScheduleVerifier.Config(config.timeRange, config.daysOfWeek, config.negate) }
    }

    override fun verify(): Mono<Success> {
       anxietyDatabase.transaction {
             Anxieties.slice(Anxieties.description)
                .selectAll()
                .orderBy(Anxieties.created, SortOrder.DESC)
                .map { it[Anxieties.description] to it[Anxieties.created] }
                .toList()
        }.onFailure {
            return Mono.error(it)
       }.onSuccess {
           return it.result()
       }
        return Mono.empty() // unreachable
    }

    fun List<Pair<String, Instant>>.result(): Mono<Success>{
        val schedule = verifier.verify().hasElement().block(Duration.ofSeconds(10)) ?: false
        if (!schedule) return Mono.empty() // if not within schedule abort

        val last = maxByOrNull { it.second } ?: return Mono.empty()
        lastEntryExpired(last)?.let { return Mono.just(it) } // if expired, request new
        lastEntryInvalid(last, this)?.let { return Mono.just(it) } // if not expired but invalid still request

        return Mono.empty() // do not request if not expired and valid
    }

    fun lastEntryExpired(last: Pair<String, Instant>): Success? {
        val expiry: Instant = last.second.plus(config.expiry.toKotlinDuration())
        val now: Instant = Clock.System.now()

        if (now <= expiry) return null

        return Success(
            expiry = expiry.toJavaInstant(),
            reason = "Last entry was more than ${config.expiry.toDays()} days ago: ${last.first.substring(0, 15)} on ${last.second}"
        )
    }

    fun lastEntryInvalid(last: Pair<String, Instant>, entries: List<Pair<String, Instant>>): Success? {
        isWordCountInvalid(last.first)?.let {
            return Success(
                "Word count of last entry was not enough: $it",
                java.time.Instant.MAX
            )
        }

        val detector = PlagiarismDetector(entries
            .filter { it != last }
            .map { it.first }, config.cosineThreshold, config.jaccardThreshold)

        val plagiarized = detector.isPlagiarized(last.first) ?: return null

        return Success(
            "Last entry was plagiarized: $last vs $plagiarized",
            java.time.Instant.MAX
        )
    }

    fun isWordCountInvalid(description: String):Int?{
        val cnt = countWords(description)
        return if(cnt >= config.minWords) null else cnt
    }

    fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split("\\s+".toRegex()).size
    }

}

class PlagiarismDetector(
    private val existingDescriptions: List<String>,
    private val cosineThreshold: Double = 0.7,
    private val jaccardThreshold: Double = 0.6
) {
    private val cosineSimilarity = CosineSimilarity()
    private val jaccardSimilarity = JaccardSimilarity()

    // Thresholds (adjust based on your needs)
    
    fun score(newDescription: String): List<Triple<Double, Double, String>>{
        return existingDescriptions.map {
            val cosineSim = cosineSimilarity.cosineSimilarity(
                tokenize(it),
                tokenize(newDescription)
            )
            val jaccardSim = jaccardSimilarity.apply(it, newDescription)
            Triple(cosineSim,jaccardSim, it)
        }
    }

    fun isPlagiarized(newDescription: String): String? {
        return score(newDescription).firstOrNull { (cosineSim, jaccardSim) ->
            cosineSim >= cosineThreshold || jaccardSim >= jaccardThreshold
        }?.third
    }

    private fun tokenize(text: String): Map<CharSequence, Int> {
        return text.split("\\s+".toRegex())
            .groupingBy { it }
            .eachCount()
    }
}

object Anxieties : Table("anxieties") {
    val description: Column<String> = varchar("description", 4096)
    val created: Column<Instant> = timestamp("created")
}