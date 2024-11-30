package io.github.coden.impulse.judge

import io.github.coden.impulse.judge.Match.Companion.ifFailed
import io.github.coden.wellpass.api.CheckIns
import net.bytebuddy.asm.Advice.Local
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class WellpassRule : TimeDependentRule<CheckIns> {
    override fun test(entity: CheckIns): Match {
        val checkIns = entity.checkIns
        val last = checkIns.maxByOrNull { it.checkInDate }
        return checkIns
            .filter { it.name.lowercase().contains("yoga|boulder|fitness first|kletter|fit/one".toRegex()) }
            .any { it.checkInDate.isAfter(LocalDateTime.now().minusDays(5)) }
            .ifFailed("Last checkin was more than 5 days ago:  ${last?.name} on ${last?.checkInDate}")
    }

    override fun nextAllowed(entity: CheckIns): LocalDateTime {
        return nextDisallowed(entity)
    }

    override fun nextDisallowed(entity: CheckIns): LocalDateTime {
        val checkIns = entity.checkIns
        val last = checkIns.maxOfOrNull { it.checkInDate } ?: return Instant.EPOCH.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val nextMax = last.plus(5.days.toJavaDuration())

        return nextMax
    }
}