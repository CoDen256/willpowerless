package io.github.coden256.wpl.judge.laws

import io.github.coden256.wellpass.api.CheckIn
import io.github.coden256.wellpass.api.Wellpass
import io.github.coden256.wpl.judge.config.RulingRegistry
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verdict
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.print.attribute.standard.MediaSize.NA
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration


@Component
@EnableConfigurationProperties(ForceGymLaw.Cfg::class)
@ConditionalOnProperty(value = ["laws.${ForceGymLaw.NAME}.enabled"], matchIfMissing = true)
class ForceGymLaw(
    private val registry: RulingRegistry,
    private val wellpass: Wellpass,
    private val config: Cfg
) : Law, Logging {
    companion object {
        const val NAME = "force-gym"
    }

    @ConfigurationProperties(prefix = "laws.${NAME}")
    data class Cfg(
        val description: String,
        val rulings: List<String>,
        val expiry: Duration,
        val enabled: Boolean = true,
        val cache: Duration = Duration.ofHours(1)
    )

    val checkins = Mono
        .defer {
            val today = LocalDate.now()
            logger.info("[$NAME] Requesting gym checkins as of ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}...")
            wellpass
                .checkins(today.minusMonths(1), today)
                .timeout(Duration.ofSeconds(60))
        }
        .cache(config.cache)

    override fun rulings() = registry.getRules(config.rulings)

    override fun verify(): Mono<Verdict> {
        val now = LocalDateTime.now()
        return checkins
            .map {
                val last = it.checkIns.filter { isValidGym(it) }.maxByOrNull { it.checkInDate }
                val expiry = last?.checkInDate?.plus(config.expiry) ?: LocalDateTime.MIN
                val enabled = now.isAfter(expiry)

                Verdict(
                    rulings(),
                    enabled = enabled,
                    expires = expiry,
                    reason = "Last checkin was more than ${config.expiry.toDays()} days ago: ${last?.name} on ${last?.checkInDate}",
                    law = NAME
                )
            }
    }

    override fun name(): String {
        return NAME
    }

    private fun isValidGym(it: CheckIn) =
        it.name.lowercase().contains("yoga|boulder|fitness first|kletter|fit/one".toRegex()) &&
                it.name.lowercase().contains("leipzig|plagwitz".toRegex())

}