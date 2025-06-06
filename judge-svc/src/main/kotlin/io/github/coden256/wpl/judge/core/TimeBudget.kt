package io.github.coden256.wpl.judge.core

import io.github.coden256.utils.notNullOrFailure
import io.github.coden256.utils.success
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import kotlin.time.Duration

interface TimeTracker {
    fun start(): Result<Instant>
    fun stop(): Result<Session>
    fun sessions(): List<Session>
}

interface Budget {
    fun request(sessions: List<Session>): Duration
}

interface RealtimeBudget {
    fun request(): Duration
}

class TimeTrackerAwareBudget(private val tracker: TimeTracker, private val budget: Budget): RealtimeBudget{
    override fun request(): Duration {
        return budget.request(tracker.sessions())
    }
}

data class Session(val start: Instant, val stop: Instant)


class InMemoryTimeTracker(initSessions: List<Session> = listOf(),
                          private var current: Instant? = null): TimeTracker {
    private val sessions = initSessions.toMutableList()

    override fun start(): Result<Instant> {
        return current?.let { Result.failure(IllegalStateException("Clock already started at $it")) } // if present -> fail
                ?: Clock.System.now().also { current = it }.success() // if absent -> set
    }

    override fun stop(): Result<Session> {
        return currentSession()
            ?.also { sessions.add(it) }
            ?.also { current = null }
            .notNullOrFailure(IllegalStateException("Clock was not started"))
    }

    override fun sessions(): List<Session> {
        return currentSession()?.let { sessions.plus(it) } ?: sessions
    }

    private fun currentSession(): Session? {
        return current?.let { Session(it, Clock.System.now()) }
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