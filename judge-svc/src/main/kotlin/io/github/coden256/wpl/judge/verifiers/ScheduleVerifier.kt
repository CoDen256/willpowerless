package io.github.coden256.wpl.judge.verifiers

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
        TODO("Not yet implemented")
    }
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