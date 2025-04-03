package io.github.coden256.wpl.judge.bot.telegram.bot

import io.github.coden256.telegram.abilities.BaseTelegramBot
import io.github.coden256.telegram.abilities.TelegramBotConfig
import io.github.coden256.wpl.judge.bot.telegram.db.JudgeBotDB
import io.github.coden256.wpl.judge.bot.telegram.formatter.JudgeFormatter


class JudgeTelegramBot(
    config: TelegramBotConfig,
    db: JudgeBotDB,
    private val formatter: JudgeFormatter,
) : BaseTelegramBot<JudgeBotDB>(config, db) {


}