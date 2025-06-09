package io.github.coden256.wpl.judge.budgets

import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap
import io.github.coden256.wpl.judge.core.Budget
import io.github.coden256.wpl.judge.core.Session
import io.github.coden256.wpl.judge.core.Session.Companion.sum
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class DailyBudget(
    private val budget: Duration,
    private val tz: TimeZone = TimeZone.of("CET")
): Budget {

    override fun request(sessions: List<Session>): RangeMap<Instant, Duration> {
        val rangeMap = TreeRangeMap.create<Instant, Duration>()

        // First set the default value (full budget)
        rangeMap.put(Range.all(), budget)

        if (sessions.isEmpty()) {
            return rangeMap
        }

        // Group sessions by day
        val sessionsByDay = sessions.groupBy { session ->
            session.start.toLocalDateTime(tz).date
        }

        // For each day with sessions, calculate remaining budget
        sessionsByDay.forEach { (date, daySessions) ->
            val dayStart = date.atStartOfDayIn(tz)
            val dayEnd = (date.atStartOfDayIn(tz) + 1.days)

            val totalUsage = daySessions.sum()
            val remaining = budget - totalUsage

            // If remaining is negative, clamp at 0
            val clampedRemaining = if (remaining.isNegative()) Duration.ZERO else remaining

            // Update the range for this day
            rangeMap.put(Range.closedOpen(dayStart, dayEnd), clampedRemaining)
        }

        return rangeMap
    }
}
