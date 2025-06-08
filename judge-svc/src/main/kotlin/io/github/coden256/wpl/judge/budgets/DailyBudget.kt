package io.github.coden256.wpl.judge.budgets

import io.github.coden256.wpl.judge.core.Budget
import io.github.coden256.wpl.judge.core.InstantRange
import io.github.coden256.wpl.judge.core.Session
import kotlinx.datetime.*
import java.util.TreeMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class DailyBudget(
    private val daily: Duration,
    private val tz: TimeZone = TimeZone.of("CET")
): Budget {

    override fun request(sessions: List<Session>): TreeMap<Instant, Duration> {
        return TreeMap(calculateUsage(sessions)
            .mapValues { (_, usage) -> daily.minus(usage).coerceAtLeast(Duration.ZERO) })
    }


    private fun calculateUsage(sessions: List<Session>): TreeMap<Instant, Duration> {
        return TreeMap(ranges(sessions).associateWith { range ->
            return@associateWith sessions.filter { session ->
                session.start < range && session.stop > range.plus(1.days)
            }.map {
                val overlapStart = maxOf(it.start, range)
                val overlapEnd = minOf(it.stop, range.plus(1.days))
                overlapEnd - overlapStart
            }.reduce(Duration::plus)
        })
    }

    private fun ranges(sessions: List<Session>): List<Instant>{
        return sessions.map { it.start }
    }
}