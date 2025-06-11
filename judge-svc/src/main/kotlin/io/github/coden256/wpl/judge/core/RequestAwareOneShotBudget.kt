package io.github.coden256.wpl.judge.core

import io.github.coden256.utils.success
import kotlinx.datetime.*
import org.apache.commons.lang3.Range
import java.io.File
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration


interface OneShotBudget {
    fun request(): Result<Unit> = request(Clock.System.now())
    fun request(ref: Instant): Result<Unit>
    fun remaining(): Duration = remaining(Clock.System.now())
    fun remaining(ref: Instant): Duration
}

interface SessionStorage {
    fun set(current: Instant)
    fun get(): Result<Instant>
    fun clear()
}

class SessionFileStorage(private val file: File) : SessionStorage {
    override fun set(current: Instant) {
        verify()
        file.writeText(current.toString(), Charsets.UTF_8)
    }

    override fun get(): Result<Instant> {
        verify()
        val content = file.readText(Charsets.UTF_8)
        if (content.isBlank()) return Result.failure(IllegalStateException("Empty file content"))
        val current = Instant.parse(content)
        return current.success()
    }

    override fun clear() {
        file.writeText("")
    }

    private fun verify() {
        if (!file.exists()) file.createNewFile()
    }

}

class RequestAwareOneShotBudget(
    private val storage: SessionStorage,
    private val budget: (LocalDateTime) -> Duration,
    private val rangeStart: LocalTime,
    private val rangeLength: Duration,
    private val tz: TimeZone = TimeZone.of("CET")
) : OneShotBudget {
    private val rangeEnd =
        LocalTime.fromSecondOfDay(((rangeStart.toSecondOfDay() + rangeLength.inWholeSeconds) % java.time.LocalTime.MAX.toSecondOfDay() - 1).toInt())

    override fun request(ref: Instant): Result<Unit> {
        val start = storage.get().getOrNull()
        if (isInSession(start, ref)) return Result.failure(IllegalStateException("Session already exists: started at ${start?.toLocalDateTime(tz)}"))
        if (!isInsideRange(ref)) return Result.failure(IllegalArgumentException("Request is outside of the range: [$rangeStart - ${LocalTime.fromNanosecondOfDay(rangeEnd.toNanosecondOfDay()+1)}]"))
        storage.set(ref)
        return Result.success(Unit)
    }

    override fun remaining(ref: Instant): Duration {
        val current = storage.get().getOrNull() ?: return Duration.ZERO
        val budget = budget(current.toLocalDateTime(tz))
        return budget - (ref - current)
    }

    fun isInsideRange(now: Instant): Boolean {
        val local = now.toLocalDateTime(tz).time
        if (rangeStart <= rangeEnd) return local in rangeStart..rangeEnd
        return local in rangeStart..java.time.LocalTime.MAX.toKotlinLocalTime() ||
                local <= rangeEnd
    }

    fun getRangeStart(session: Instant): Instant {
        val local = session.toLocalDateTime(tz)
        val time = local.time
        return (if (time >= rangeStart) local.date.atTime(rangeStart) else
            local.date.minus(1, DateTimeUnit.DAY).atTime(rangeStart))
            .toInstant(tz)
    }

    private fun isInSession(start: Instant?, now: Instant): Boolean {
        if (start == null) return false
        val rangeStart = getRangeStart(start)
        return now <= rangeStart + rangeLength
    }
}

class MinMaxBudget(
    private val rangeStart: LocalTime,
    private val rangeLength: Duration,
    private val minMaxBudget: Range<Duration>,
    private val gamma: Double
): (LocalDateTime) -> Duration {

    private val untilDayEnd = 1.days.inWholeMilliseconds - rangeStart.toMillisecondOfDay()

    override fun invoke(ref: LocalDateTime): Duration {
        val elapsed = getSinceStart(ref).inWholeMilliseconds
        val normalized = (elapsed.toDouble() / rangeLength.inWholeMilliseconds).pow(gamma)
        val min = minMaxBudget.minimum.inWholeMilliseconds
        val max = minMaxBudget.maximum.inWholeMilliseconds
        return (max + (min - max) * normalized).milliseconds
    }

    fun getSinceStart(ref: LocalDateTime): Duration {
        return if (ref.time >= rangeStart) (ref.time.toMillisecondOfDay() - rangeStart.toMillisecondOfDay()).milliseconds
        else (ref.time.toMillisecondOfDay() + untilDayEnd).milliseconds
    }
}

fun main_() {
    val store = SessionFileStorage(File("session"))
    val rangeStart = LocalTime(20, 30)
    val rangeLen = 5.hours + 30.minutes
    val tz = TimeZone.of("CET")
    (1..30).forEach {
        val curve =   it*0.1
        println("---$curve---")
        val budget = MinMaxBudget(rangeStart, rangeLen, Range.of(
            30.minutes, 3.hours + 30.minutes,
        ), curve)
        val base = LocalDateTime.parse("2007-12-03T20:30").toJavaLocalDateTime()
        (0..14*2).forEach {
            val cur = base.plus((it*15).toLong(), ChronoUnit.MINUTES).toKotlinLocalDateTime()
            val b = budget.invoke(cur).toJavaDuration().truncatedTo(ChronoUnit.MINUTES).toKotlinDuration()
            println(cur.toString().drop(5) + " --> " + cur.toInstant(tz).plus(b).toLocalDateTime(tz).toString().drop(5) + ": "+ b)
        }
        println()
    }
}