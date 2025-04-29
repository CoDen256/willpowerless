package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wellpass.CheckIn
import io.github.coden256.wellpass.Wellpass
import io.github.coden256.wellpass.config.WellpassConfiguration
import jakarta.annotation.PostConstruct
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Component
@Import(WellpassConfiguration::class)
class WellpassVerifier(
    private val wellpass: Wellpass,
) : Verifier<WellpassVerifier.Config>, Logging {
    data class Config(val expiry: Duration = Duration.ofDays(5),
                      val cache: Duration = Duration.ofHours(24)): VerifierConfig

    override var definition: VerifierDefinition? = null
    override var config: Config = Config()


    var cache = config.cache

    val checkins = Mono
        .defer {
            val today = LocalDate.now(ZoneId.of("CET"))
            logger.info("Requesting gym checkins as of ${LocalDateTime.now(ZoneId.of("CET")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}...")
            wellpass
                .checkins(today.minusMonths(1), today)
                .timeout(Duration.ofSeconds(60))
        }
        .cache(config.cache)

    @PostConstruct
    fun init(){
        cache = config.cache
    }

    override fun verify(): Mono<Success> {
        val now = LocalDateTime.now(ZoneId.of("CET"))
        return checkins
            .mapNotNull {
                val last = it.checkIns.filter { isValidGym(it) }.maxByOrNull { it.checkInDate }
                val expiry = last?.checkInDate?.plus(config.expiry) ?: LocalDateTime.MIN
                if(!now.isAfter(expiry)) return@mapNotNull null

                Success(
                    expiry = expiry.atZone(ZoneId.of("CET")).toInstant(),
                    reason = "Last checkin was more than ${config.expiry.toDays()} days ago: ${last?.name} on ${last?.checkInDate}"
                )
            }
    }

    private fun isValidGym(it: CheckIn) =
        it.name.lowercase().contains("yoga|boulder|fitness first|kletter|fit/one".toRegex()) &&
                it.name.lowercase().contains("leipzig|plagwitz".toRegex())

}