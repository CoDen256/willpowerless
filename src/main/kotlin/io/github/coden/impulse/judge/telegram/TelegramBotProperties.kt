package io.github.coden.impulse.judge.telegram

import io.github.coden.telegram.abilities.TelegramBotConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram")
data class TelegramBotProperties(val config: TelegramBotConfig)
