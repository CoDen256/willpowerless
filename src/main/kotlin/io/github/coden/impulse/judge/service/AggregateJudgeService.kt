package io.github.coden.impulse.judge.service

import io.github.coden.impulse.judge.Rule
import io.github.coden.impulse.judge.TimeDependentRule
import io.github.coden.impulse.judge.WellpassRule
import io.github.coden.wellpass.api.CheckIns
import io.github.coden.wellpass.api.Wellpass
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDate

@Service
class AggregateJudgeService(
    private val wellpass: Wellpass
): JudgeService {

    val wellpassRule: TimeDependentRule<CheckIns> = WellpassRule()

    override fun check(hard: Boolean): Mono<Verdict> {
        return wellpass
            .checkins(LocalDate.now().minusMonths(4), LocalDate.now())
            .timeout(Duration.ofSeconds(60))
            .map {
                val match = wellpassRule.test(it)
                val innocent = match.allowed || hard
                Verdict(!innocent, match.reason, it, wellpassRule.nextDisallowed(it))
            }
    }
}