package io.github.coden256.judge.rules

import io.github.coden256.calendar.api.Absence
import io.github.coden256.judge.api.Match
import io.github.coden256.judge.api.Match.Companion.asMatch
import io.github.coden256.judge.api.Rule
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class AbsenceRule: Rule<List<Absence>> {

    override fun test(entity: List<Absence>): Match {
        val now = LocalDateTime.now()
        val latestAbsence = entity
            .maxByOrNull { it.end }
            ?: Absence("n/a", LocalDateTime.MIN, LocalDateTime.MIN)

        val extra = getExtraDuration(latestAbsence.duration())

        return now
            .isBefore(latestAbsence.end.plus(extra.toJavaDuration()))
            .asMatch()
            .onFail("❌ Is not sick or on vacation: ${latestAbsence.end} + $extra")
            .onSuccess("✅ Is sick or on vacation: ${latestAbsence.end} + $extra")
    }

    private fun getExtraDuration(length: kotlin.time.Duration): kotlin.time.Duration {
        return (length.inWholeHours / 1.8).coerceIn(0.0, 7.days.inWholeMinutes.toDouble()).hours
    }
}

fun Absence.duration(): kotlin.time.Duration{
    return Duration.between(start, end).toKotlinDuration()
}