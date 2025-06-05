package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wellpass.CheckIn
import io.github.coden256.wellpass.CheckIns
import io.github.coden256.wellpass.Wellpass
import io.github.coden256.wellpass.config.WellpassConfiguration
import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
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
) : Verifier<WellpassVerifier.Config>(), Logging {
    data class Config(val expiry: Duration, val cache: Duration, val regex: Regex) : VerifierConfig

    private val checkins: Mono<CheckIns> by lazy { // MUST be lazy since config is initialized only after init method
        Mono.defer {
            logger.info("Requesting gym checkins as of ${LocalDateTime.now(ZoneId.of("CET")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}...")
            val today = LocalDate.now(ZoneId.of("CET"))
            wellpass.checkins(today.minusMonths(1), today)
        }
            .timeout(Duration.ofSeconds(60))
            .cache(config.cache)
    }

    override fun verify(): Mono<Success> {
        val now = LocalDateTime.now(ZoneId.of("CET"))
        return checkins
            .mapNotNull {
                val last = it.checkIns.filter { isValidGym(it) }.maxByOrNull { it.checkInDate }
                val expiry = last?.checkInDate?.plus(config.expiry) ?: LocalDateTime.MIN
                if (!now.isAfter(expiry)) return@mapNotNull null

                Success(
                    expiry = expiry.atZone(ZoneId.of("CET")).toInstant(),
                    reason = "Last checkin was more than ${config.expiry.toDays()} days ago: ${last?.name} on ${last?.checkInDate}"
                )
            }
    }

    private fun isValidGym(it: CheckIn) = it.name.lowercase().matches(config.regex)
}