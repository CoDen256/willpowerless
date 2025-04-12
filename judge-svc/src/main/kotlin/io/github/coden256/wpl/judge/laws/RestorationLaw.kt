package io.github.coden256.wpl.judge.laws

import io.github.coden256.calendar.api.Absence
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.wpl.judge.api.Match
import io.github.coden256.wpl.judge.api.Match.Companion.asMatch
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.RulingRegistry
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class RestorationLaw(
    private val registry: RulingRegistry,
    private val calendar: Calendar
): Law {

    override fun isEnabled(): Match {
        val now = LocalDateTime.now()
        val latestAbsence = getLongAbsences()
            .maxByOrNull { it.end }
            ?: Absence("n/a", LocalDateTime.MIN, LocalDateTime.MIN)

        val extra = getExtraDuration(latestAbsence.duration())
        return now
            .isBefore(latestAbsence.end.plus(extra.toJavaDuration()))
            .asMatch()
            .onFail("⛔ Is not sick or on vacation: ${latestAbsence.end} + $extra")
            .onSuccess("✅ Is sick or on vacation: ${latestAbsence.end} + $extra")
    }

    override fun expires(): LocalDateTime {
        TODO("Not yet implemented")
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

    private fun getExtraDuration(length: kotlin.time.Duration): kotlin.time.Duration {
        return (length.inWholeHours / 1.8).coerceIn(0.0, 7.days.inWholeMinutes.toDouble()).hours
    }

    private fun Absence.duration(): kotlin.time.Duration{
        return Duration.between(start, end).toKotlinDuration()
    }

    override fun rulings() = registry.getRules("sick-or-vacation")
}

