package io.github.coden256.wpl.judge.bot.telegram.api

import io.github.coden256.wpl.judge.bot.telegram.HttpJudge
import kotlin.time.Duration

interface BudgetEditor {
    fun request(): Result<Unit>
    fun remaining(): Result<Duration>
}

class BaseBudgetEditor(val httpJudge: HttpJudge): BudgetEditor{
    override fun request(): Result<Unit> {
        return httpJudge.request().map {  }
    }

    override fun remaining(): Result<Duration> {
        return httpJudge.remaining()
    }

}