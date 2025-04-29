package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.verifiers.api.Success
import io.github.coden256.wpl.judge.verifiers.api.Verifier
import io.github.coden256.wpl.judge.verifiers.api.VerifierConfig
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CalenderAbsenceVerifier: Verifier<CalenderAbsenceVerifier.Config> {
    data class Config(val expiryToDurationRate: Double,
                      val maxExpiry: java.time.Duration,
                      val minExpiry: java.time.Duration): VerifierConfig()

    override lateinit var config: Config

    override fun verify(): Mono<Success> {
        TODO("Not yet implemented")
    }
}