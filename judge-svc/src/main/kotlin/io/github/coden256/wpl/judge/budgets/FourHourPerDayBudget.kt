package io.github.coden256.wpl.judge.budgets

//class FourHourPerDayBudget: Budget {
//    override fun request(sessions: List<Session>): Duration {
//        val now = Clock.System.now()
//
//        val spent = sessions
//            .filter { now - it.stop <= 24.hours }
//            .map { it.stop - it.start }
//            .reduce(Duration::plus)
//
//        return 4.hours - spent
//    }
//
//    private fun List<Session>.getHistoricalSpentTime(): Duration {
//        return map { it.stop - it.start }.reduce(Duration::plus)
//    }
//}