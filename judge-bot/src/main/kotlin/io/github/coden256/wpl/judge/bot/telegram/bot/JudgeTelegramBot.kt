package io.github.coden256.wpl.judge.bot.telegram.bot

import io.github.coden256.telegram.abilities.*
import io.github.coden256.telegram.db.Chat.Companion.asChat
import io.github.coden256.telegram.senders.answerCallback
import io.github.coden256.telegram.senders.send
import io.github.coden256.wpl.judge.bot.telegram.HttpJudge
import io.github.coden256.wpl.judge.bot.telegram.api.BudgetEditor
import io.github.coden256.wpl.judge.bot.telegram.db.JudgeBotDB
import io.github.coden256.wpl.judge.bot.telegram.formatter.JudgeFormatter
import org.apache.logging.log4j.kotlin.Logging
import org.telegram.abilitybots.api.objects.Reply
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration


class JudgeTelegramBot(
     config: TelegramBotConfig,
     db: JudgeBotDB,
     private val budgetEditor: BudgetEditor,
    private val formatter: JudgeFormatter,
) : BaseTelegramBot<JudgeBotDB>(config, db) {

    override fun start() = ability("start") {
        sender.send(config.intro, config.target.asChat(), withRequestButtons())
    }

    fun onCallback(): Reply = replyOnCallback { update, data ->
        println("Handling callback for $data")
        when (data) {
            REQUEST_BUDGET.data -> request(update)
            GET_BUDGET.data -> replyGetBudget(update)
        }
        sender.answerCallback(update, "ok")
    }

    private fun request(upd: Update){
        budgetEditor.request()
            .onSuccess { sender.send("✅ Success:\n\n$it", upd.chat()) }
            .onFailure { sender.send("⚠\uFE0FFailed to create budget: \n\n${it.message}", upd.chat())  }
        replyGetBudget(upd)
    }

    private fun replyGetBudget(upd: Update){
        budgetEditor.remaining()
            .onSuccess { sender.send("✅ Budget available! \nRemaining: $it\n\nExpiry: \uD83D\uDD59 ${getExpiryDateTime(it)}", upd.chat()) }
            .onFailure {
                if (it is HttpJudge.BudgetExceededException){
                    sender.send("❌ ${it.message}\nRemaining: ${it.budget}\n\nExpiry: \uD83D\uDD59 ${getExpiryDateTime(it.budget)}", upd.chat())
                } else {
                    sender.send("⚠\uFE0FUnable to get budget info:\n\n${it.message}", upd.chat())
                }
            }
    }

    private fun getExpiryDateTime(duration: Duration): LocalTime? {
        return LocalDateTime.now(ZoneId.of("CET")).plusNanos(duration.inWholeNanoseconds).toLocalTime()
    }

}