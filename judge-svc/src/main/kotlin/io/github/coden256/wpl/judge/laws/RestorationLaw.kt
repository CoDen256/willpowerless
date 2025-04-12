package io.github.coden256.wpl.judge.laws

import io.github.coden256.calendar.api.Absence
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.wpl.judge.config.RulingRegistry
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verdict
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration


@Component
@EnableConfigurationProperties(RestorationLaw.Cfg::class)
@ConditionalOnProperty(value = ["laws.${RestorationLaw.NAME}.enabled"], matchIfMissing = true)
class RestorationLaw(
    private val registry: RulingRegistry,
    private val calendar: Calendar,
    private val config: Cfg
) : Law, Logging {
    companion object { const val NAME = "allow-rest" }

    @ConfigurationProperties(prefix = "laws.${NAME}")
    data class Cfg(
        val description: String,
        val rulings: List<String>,
        val expiryToDurationRate: Double,
        val maxExpiry: java.time.Duration,
        val minExpiry: java.time.Duration,
        val enabled: Boolean = true
    )

    override fun rulings() = registry.getRules(config.rulings)

    override fun verify(): Mono<Verdict> {
        val now = LocalDateTime.now()
        val latestAbsence = getLongAbsences()
            .maxByOrNull { it.end }
            ?: Absence("n/a", LocalDateTime.MIN, LocalDateTime.MIN)

        val extra = getExtraDuration(latestAbsence.duration())
        val expiry = latestAbsence.end.plus(extra.toJavaDuration())
        val enabled = now.isBefore(expiry)

        return Mono.just(Verdict(
            rulings(),
            enabled = enabled,
            expires = expiry,
            reason = "Is sick or on vacation: ${latestAbsence.end} + $extra",
            law = NAME
        ))
    }


    // sick leaves or vacations
    private fun getLongAbsences(): List<Absence> {
        return try {
            calendar.absences()
                .filter { it.start.isBefore(LocalDateTime.now()) }
                .filter { it.duration() >= 23.9.hours }
                .map {
                    when (it.end.dayOfWeek) {
                        DayOfWeek.THURSDAY -> it.copy(end = it.end.plusDays(3))
                        DayOfWeek.FRIDAY -> it.copy(end = it.end.plusDays(2))
                        DayOfWeek.SATURDAY -> it.copy(end = it.end.plusDays(1))
                        else -> it
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getExtraDuration(length: Duration):Duration {
        return (length.inWholeHours / config.expiryToDurationRate).hours
            .coerceIn(
                config.minExpiry.toKotlinDuration(),
                config.maxExpiry.toKotlinDuration()
            )
    }

    private fun Duration.coerceIn(min: Duration, max: Duration): Duration {
        return this.inWholeNanoseconds.coerceIn(min.inWholeNanoseconds, max.inWholeNanoseconds).nanoseconds
    }

    private fun Absence.duration(): Duration {
        return java.time.Duration.between(start, end).toKotlinDuration()
    }
}

