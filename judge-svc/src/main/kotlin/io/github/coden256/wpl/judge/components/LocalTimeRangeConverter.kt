package io.github.coden256.wpl.judge.components

import org.apache.commons.lang3.Range
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.LocalTime

@ConfigurationPropertiesBinding
@Component
class LocalTimeRangeConverter : Converter<String, Range<LocalTime>> {
    override fun convert(source: String): Range<LocalTime> {
        try {
            return parseTimeRange(source)
        } catch (e: Exception) {
            throw Exception("Unable to parse local time range: $source", e)
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