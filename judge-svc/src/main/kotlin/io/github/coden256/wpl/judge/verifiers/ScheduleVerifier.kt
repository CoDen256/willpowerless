package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.core.Success
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import org.apache.commons.lang3.Range
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.time.LocalTime

@Component
class ScheduleVerifier: Verifier<ScheduleVerifier.Config>() {
    data class Config(val timeRange: Range<LocalTime>,
                      val daysOfWeek: List<DayOfWeek>,
                      val negate: Boolean = false): VerifierConfig

    override fun verify(): Mono<Success> {
//        val current = LocalDateTime.now(ZoneId.of("CET"))
//        val enabled = config.schedule.any { it.matches(current) } || config.schedule.isEmpty()
//        val reason = if (config.schedule.isNotEmpty()) ": checking schedule=${config.schedule}" else ""
//        return Mono.just(
//            Verdict(
//                rulings(),
//                enabled = enabled,
//                expires = LocalDateTime.MAX,
//                reason = config.description +  reason,
//                law = name
//            )
//        )
        TODO("Not yet implemented")
    }

//    fun matches(current: LocalDateTime): Boolean {
//        val match = timeRange.contains(current.toLocalTime()) && daysOfWeek.contains(current.dayOfWeek)
//        return (match && !negate) || (!match && negate)
//    }
}

@ConfigurationPropertiesBinding
@Component
class LocalTimeRangeConverter: Converter<String, Range<LocalTime>> {
    override fun convert(source: String): Range<LocalTime> {
        try {
            return  parseTimeRange(source)
        } catch (e: Exception) {
            throw Exception("app.callback-mappings property is invalid. Must be a JSON object string")
        }
    }

    private fun parseTimeRange(range: String): Range<LocalTime> {
        val (start, end) = range
            .split("-")
            .map { it.trim() }
            .map { LocalTime.parse(it) }
        return Range.of(start, end)
    }
}