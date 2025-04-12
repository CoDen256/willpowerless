package io.github.coden256.wpl.judge

import io.github.coden256.wellpass.api.config.WellpassConfiguration
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.calendar.ICSCalendar
import io.github.coden256.wpl.judge.core.RulingRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

@Configuration
@Import(WellpassConfiguration::class)
class JudgeConfiguration {
    @Bean
    fun calendar(@Value("\${api.calendar.ics}") url: String): Calendar {
        return ICSCalendar(url)
    }

    @Bean
    fun registry(environment: Environment): RulingRegistry {
        return null!!
    }
}