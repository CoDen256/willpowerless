package io.github.coden256.judge

import io.github.coden.wellpass.api.config.WellpassConfiguration
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.calendar.ICSCalendar
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(WellpassConfiguration::class)
class JudgeConfiguration {
    @Bean
    fun calendar(@Value("\${absence.ics}") url: String): Calendar {
        return ICSCalendar(url)
    }
}