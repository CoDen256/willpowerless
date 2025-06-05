package io.github.coden256.wpl.judge.core

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

interface TimeTracker {
    fun start()
    fun stop()
    fun sessions(): List<Session>
}

interface Budget {
    fun request(sessions: List<Session>): Duration
}

interface TimeAwareBudget {
    fun request(): Duration
}

class TimeTrackerAwareBudget(val tracker: TimeTracker, val budget: Budget, ): TimeAwareBudget{
    override fun request(): Duration {
        return budget.request(tracker.sessions())
    }
}

data class Session(val start: Instant, val stop: Instant)


class MemoryTimeTracker: TimeTracker {
    private val sessions = mutableListOf<Session>()
    private var current: Instant? = null

    override fun start() {
        current = Clock.System.now()
    }

    override fun stop() {
        currentSession()?.let { sessions.add(it) }
        current = null
    }

    override fun sessions(): List<Session> {
        return currentSession()?.let { sessions.plus(it) } ?: sessions
    }

    private fun currentSession(): Session? {
        return current?.let { Session(it, Clock.System.now()) }
    }
}


class FourHourPerDayBudget: Budget {
    override fun request(sessions: List<Session>): Duration {
        val now = Clock.System.now()

        val spent = sessions
            .filter { now - it.stop <= 24.hours }
            .map { it.stop - it.start }
            .reduce(Duration::plus)

        return 4.hours - spent
    }

    private fun List<Session>.getHistoricalSpentTime(): Duration {
        return map { it.stop - it.start }.reduce(Duration::plus)
    }
}