package io.github.coden.impulse.judge.telegram

import io.github.coden.impulse.judge.service.JudgeService
import io.github.coden.telegram.abilities.BaseTelegramBot
import io.github.coden.telegram.abilities.TelegramBotConfig
import io.github.coden.telegram.abilities.ability
import io.github.coden.telegram.abilities.chat
import io.github.coden.telegram.db.Chat.Companion.asChat
import io.github.coden.telegram.senders.send
import org.telegram.abilitybots.api.objects.Ability

class JudgeTelegramBot(
    config: TelegramBotConfig,
    db: JudgeDB,
    private val service: JudgeService
) : BaseTelegramBot<JudgeDB>(config, db), Notifier {


    fun check(): Ability = ability("check") { upd ->
        val hard = upd.message.text.split(" ").getOrNull(1)?.toBooleanStrictOrNull() ?: false
        service
            .check(hard)
            .subscribe {
                sender.send(it.toString(), upd.chat())
            }
    }

    override fun notify(message: String) {
        sender.send(message, creatorId().asChat())
    }
}