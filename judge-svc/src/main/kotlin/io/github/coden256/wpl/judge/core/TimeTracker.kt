package io.github.coden256.wpl.judge.core

import com.google.common.collect.RangeMap
import io.github.coden256.utils.notNullOrFailure
import io.github.coden256.utils.success
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.util.TreeMap
import kotlin.time.Duration

interface TimeTracker {
    fun start(): Result<Instant>
    fun stop(): Result<Session>
    fun sessions(): List<Session>
    fun current(): Result<Session>
}

interface Budget {
    fun request(sessions: List<Session>): RangeMap<Instant, Duration>
}
data class Session(val start: Instant, val stop: Instant){
    fun duration(): Duration {
        return stop - start
    }

    companion object {
        fun Iterable<Session>.sum() = map { it.duration() }.reduce(Duration::plus)
    }
}

interface RealtimeBudget {
    fun request(): Duration
}

class TimeTrackerAwareBudget(private val tracker: TimeTracker, private val budget: Budget): RealtimeBudget{
    override fun request(): Duration {
        val history = tracker.sessions()
        val current = tracker.current()
        val now = current.map { it.stop }.getOrDefault(Clock.System.now())
        val total = current.map { history.plus(it) }.getOrDefault(history)
        val budgets = budget.request(total)
        return budgets.get(now) ?: Duration.ZERO
    }
}

class InMemoryTimeTracker(initSessions: List<Session> = listOf(),
                          private var current: Instant? = null): TimeTracker {
    private val sessions = initSessions.toMutableList()

    override fun start(): Result<Instant> {
        current()
            .onSuccess {
                return Result.failure(IllegalStateException("Clock already started at $it"))
            }
            .onFailure {
                val now = Clock.System.now()
                current = now
                return now.success()
            }
        return Result.failure(AssertionError("Unreachable statement"))
    }

    override fun stop(): Result<Session> {
        return current().onSuccess { sessions.add(it); current = null }
    }

    override fun sessions(): List<Session> {
        return sessions
    }

    override fun current(): Result<Session> {
        return current
            .notNullOrFailure(IllegalStateException("No current session"))
            .map { Session(it, Clock.System.now()) }
    }
}

class InFileTimeTracker(private val file: File): TimeTracker{

    private val delegate = load(file)

    override fun start(): Result<Instant> {
        return delegate
            .start()
            .onSuccess { file.appendText("$it;") }
    }

    override fun stop(): Result<Session> {
        return delegate
            .stop()
            .onSuccess { file.appendText(it.stop.toString()+"\n") }
    }

    override fun sessions(): List<Session> {
        return delegate.sessions()
    }

    override fun current(): Result<Session> {
        return delegate.current()
    }

    private fun load(file: File): TimeTracker {
        if (!file.exists()) {
            file.createNewFile()
            return InMemoryTimeTracker()
        }
        val lines = file.readLines()
        val sessions = lines.mapNotNull {
            val (start, stop) = it.split(";")
            if (stop.isBlank()) return@mapNotNull null
            Session(Instant.parse(start), Instant.parse(stop))
        }
        val current = lines.lastOrNull { it.trim().endsWith(";") }?.let { Instant.parse(it.split(";")[0]) }
        return InMemoryTimeTracker(sessions, current)
    }
}