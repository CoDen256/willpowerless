package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant

@Component
object AlwaysValidVerifier: Verifier<AlwaysValidVerifier.Config>() {
    object Config: VerifierConfig

    override fun verify(): Mono<Success> {
        return Mono.just(Success("always valid", Instant.MAX))
    }
}

