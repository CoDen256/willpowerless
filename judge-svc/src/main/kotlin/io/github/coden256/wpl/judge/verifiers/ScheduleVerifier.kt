package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import org.apache.commons.lang3.Range
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.*

@Component
class ScheduleVerifier: Verifier<ScheduleVerifier.Config>() {
    data class Config(val timeRange: Range<LocalTime>,
                      val daysOfWeek: List<DayOfWeek>,
                      val negate: Boolean = false): VerifierConfig

    override fun verify(): Mono<Success> {
        val current = LocalDateTime.now(ZoneId.of("CET"))
        val enabled = matches(current)
        val reason = "schedule matches: [${config.pretty()}]"

        if (!enabled) return Mono.empty()

        return Mono.just(
            Success(
                reason,
                Instant.MAX
            )
        )
    }

    fun matches(current: LocalDateTime): Boolean {
        val match = config.timeRange.contains(current.toLocalTime()) && config.daysOfWeek.contains(current.dayOfWeek)
        return (match && !config.negate) || (!match && config.negate)
    }

    private fun Config.pretty(): String {
        return "${if (!negate) "" else "!"}($timeRange, $daysOfWeek)"
    }
}

