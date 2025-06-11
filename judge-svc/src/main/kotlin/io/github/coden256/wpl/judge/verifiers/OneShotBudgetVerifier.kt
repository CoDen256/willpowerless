package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.core.OneShotBudget
import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.time.toJavaDuration

@Component
class OneShotBudgetVerifier(private val budget: OneShotBudget): Verifier<OneShotBudgetVerifier.Config>() {
    data class Config(val type: String): VerifierConfig

    override fun verify(): Mono<Success> {
        val duration = budget.remaining()
        if (!duration.isPositive()) return Mono.empty()

        return Mono.just(Success(
            "TimeBudget is available: $duration",
            Instant.now().plus(duration.toJavaDuration())
        ))
    }
}