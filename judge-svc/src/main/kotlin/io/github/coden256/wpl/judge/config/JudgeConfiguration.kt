package io.github.coden256.wpl.judge.config

import io.github.coden256.calendar.ICSCalendar
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.wpl.judge.verifiers.MultipleLawsProperties
import io.github.coden256.wpl.judge.verifiers.WellpassVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(MultipleLawsProperties::class)
class JudgeConfiguration {
    @Bean
    fun calendar(@Value("\${api.calendar.ics}") url: String): Calendar {
        return ICSCalendar(url)
    }
}

