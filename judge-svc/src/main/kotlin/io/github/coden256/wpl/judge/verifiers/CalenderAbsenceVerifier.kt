package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.calendar.api.Absence
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.wpl.judge.config.CalendarAPIConfiguration
import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

@Component
@Import(CalendarAPIConfiguration::class)
class CalenderAbsenceVerifier(
    private val calendar: Calendar
) : Verifier<CalenderAbsenceVerifier.Config>() {
    data class Config(
        val expiryToDurationRate: Double,
        val maxExpiry: java.time.Duration,
        val minExpiry: java.time.Duration
    ) : VerifierConfig

    override fun verify(): Mono<Success> {
        val now = LocalDateTime.now(ZoneId.of("CET"))
        val latestAbsence = getLongAbsences()
            .filter { it.start.isBefore(now) }
            .maxByOrNull { it.end }
            ?: Absence("n/a", LocalDateTime.MIN, LocalDateTime.MIN)

        val extra = getExtraDuration(latestAbsence.duration())
        val expiry = latestAbsence.end.plus(extra.toJavaDuration())
        val enabled = now.isBefore(expiry)

        if (!enabled) return Mono.empty()

        return Mono.just(
            Success(
                "Is sick or on vacation: ${latestAbsence.end} + $extra",
                expiry = expiry.atZone(ZoneId.of("CET")).toInstant()
            )
        )
    }

    // sick leaves or vacations
    private fun getLongAbsences(): List<Absence> {
        return try {
            (calendar.absences())
                .filter { it.duration() >= 23.9.hours }
                .map {
                    when (it.end.dayOfWeek) {
                        DayOfWeek.THURSDAY -> it.copy(end = it.end.plusDays(3))
                        DayOfWeek.FRIDAY -> it.copy(end = it.end.plusDays(2))
                        DayOfWeek.SATURDAY -> it.copy(end = it.end.plusDays(1))
                        else -> it
                    }
                }
                .map { it.copy(start = it.start.minusDays(1)) } // preparation for vacation
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getExtraDuration(length: Duration): Duration {
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