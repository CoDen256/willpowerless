package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.anxiety.AnxietyDatabaseConfiguration
import io.github.coden256.database.transaction
import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Import(AnxietyDatabaseConfiguration::class)
@Component
class AnxietyJournalVerifier(
    private val anxietyDatabase: Database
): Verifier<AnxietyJournalVerifier.Config>() {

    data class Config(val minChars: Long): VerifierConfig

    override fun verify(): Mono<Success> {
       anxietyDatabase.transaction {
             Anxieties.slice(Anxieties.description)  // Select only description column
                .selectAll()               // All rows
                .orderBy(Anxieties.created, SortOrder.DESC)  // Newest first
                .limit(1)                  // Just one row
                .map { it[Anxieties.description] to it[Anxieties.created] }  // Extract description
                .single()
        }.onFailure {
            return Mono.error(it)
       }.onSuccess {
           return it.result()
       }
        return Mono.empty()
    }

    fun Pair<String, Instant>.result(): Mono<Success>{
        val (desc, created) = this
        
    }
}

object Anxieties : Table("anxieties") {
    val description: Column<String> = varchar("description", 4096)
    val created: Column<Instant> = timestamp("created")
}