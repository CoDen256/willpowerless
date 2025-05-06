package io.github.coden256.wpl.judge.verifiers

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CalenderAbsenceVerifier: Verifier<CalenderAbsenceVerifier.Config>() {
    data class Config(val expiryToDurationRate: Double,
                      val maxExpiry: java.time.Duration,
                      val minExpiry: java.time.Duration): VerifierConfig

    override fun verify(): Mono<Success> {
        TODO("Not yet implemented")
    }
}