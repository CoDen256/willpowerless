package io.github.coden256.wpl.judge.bot

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import io.github.coden256.telegram.abilities.TelegramBotConfig
import io.github.coden256.telegram.run.TelegramBotConsole
import io.github.coden256.wpl.judge.bot.telegram.bot.JudgeTelegramBot
import io.github.coden256.wpl.judge.bot.telegram.db.JudgeBotDB
import io.github.coden256.wpl.judge.bot.telegram.formatter.JudgeTelegramFormatter

data class Config(
    val debunker: TelegramBotConfig,
)


fun config(): Config {
    return ConfigLoaderBuilder.default()
        .addResourceSource("/application.yml", optional = true)
        .addFileSource("application.yml", optional = true)
        .build()
        .loadConfigOrThrow<Config>()
}

fun main() {
    val config = config()

    val formatter = JudgeTelegramFormatter()
    val debunkerDb = JudgeBotDB("judge.db")

    val debunker = JudgeTelegramBot(
        config.debunker,
        debunkerDb,
        formatter,
    )

    val console = TelegramBotConsole(
        debunker
    )

    console.start()
}