package io.github.coden.impulse.judge

import io.github.coden.impulse.judge.service.JudgeService
import io.github.coden.impulse.judge.telegram.JudgeDB
import io.github.coden.impulse.judge.telegram.JudgeTelegramBot
import io.github.coden.impulse.judge.telegram.TelegramBotProperties
import io.github.coden.telegram.abilities.RunnableLongPollingBot
import io.github.coden.telegram.run.TelegramBotConsole
import io.github.coden.wellpass.api.config.WellpassConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration()
@Import(WellpassConfiguration::class)
@EnableConfigurationProperties(TelegramBotProperties::class)
class JudgeConfiguration {
    @Bean
    fun bot(db: JudgeDB, properties: TelegramBotProperties, service: JudgeService): JudgeTelegramBot{
        return JudgeTelegramBot(properties.config, db, service)
    }

    @Bean
    fun db(): JudgeDB {
        return JudgeDB("judge.db")
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    fun console(bots: List<RunnableLongPollingBot>): TelegramBotConsole{
        return TelegramBotConsole(*bots.toTypedArray())
    }
}