package io.github.coden256.judge.rules

import io.github.coden256.judge.api.Match
import io.github.coden256.judge.api.Match.Companion.asMatch
import io.github.coden256.judge.api.Rule
import io.github.coden256.calendar.api.Absence
import java.time.LocalDateTime

class AbsenceRule: Rule<List<Absence>> {
    override fun test(entity: List<Absence>): Match {
        val now = LocalDateTime.now()
        val latestAbsence: LocalDateTime = entity
            .maxOfOrNull { it.end }
            ?.plusDays(1)
            ?: LocalDateTime.MIN

        return now
            .isBefore(latestAbsence.plusDays(5))
            .asMatch("Is not sick")
    }
}