package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.verifiers.api.Success
import io.github.coden256.wpl.judge.verifiers.api.Verifier
import io.github.coden256.wpl.judge.verifiers.api.VerifierConfig
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.DayOfWeek

@Component
class ScheduleVerifier: Verifier<ScheduleVerifier.Config> {
    data class Config(val timeRange: String,
                      val daysOfWeek: List<DayOfWeek>,
                      val negate: Boolean = false): VerifierConfig()

    override lateinit var config: Config

    override fun verify(): Mono<Success> {
        TODO("Not yet implemented")
    }
}