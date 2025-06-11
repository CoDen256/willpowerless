package io.github.coden256.wpl.judge.components

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.LocalTime

@ConfigurationPropertiesBinding
@Component
class LocalTimeConverter : Converter<Int, LocalTime> {
    override fun convert(source: Int): LocalTime {
        try {
            return LocalTime.ofSecondOfDay(source.toLong())
        } catch (e: Exception) {
            throw Exception("Unable to parse local time range: $source", e)
        }
    }
}