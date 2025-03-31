package io.github.coden256.judge.rules

import io.github.coden.wellpass.api.CheckIn
import io.github.coden256.judge.api.Match
import io.github.coden256.judge.api.Match.Companion.asMatch
import io.github.coden256.judge.api.Rule
import io.github.coden.wellpass.api.CheckIns
import java.time.Duration
import java.time.LocalDateTime

class WellpassRule : Rule<CheckIns> {

    private val chillDuration = Duration.ofDays(5)

    override fun test(entity: CheckIns): Match {
        val checkIns = entity.checkIns
        val last = checkIns.maxByOrNull { it.checkInDate }
        return checkIns
            .filter { filterGyms(it) }
            .any { it.checkInDate.isAfter(LocalDateTime.now().minus(chillDuration)) }
            .asMatch()
            .onFail("❌ Last checkin was more than ${chillDuration} ago:  ${last?.name} on ${last?.checkInDate}")
            .onSuccess("✅ Gym alright")
    }

    private fun filterGyms(it: CheckIn) =
        it.name.lowercase().contains("yoga|boulder|fitness first|kletter|fit/one".toRegex()) &&
                it.name.lowercase().contains("leipzig|plagwitz".toRegex())
}