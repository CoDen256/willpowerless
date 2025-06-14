package io.github.coden256.wpl.judge.budgets

import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap
import com.google.common.truth.Truth.assertThat
import io.github.coden256.wpl.judge.core.Budget
import io.github.coden256.wpl.judge.core.Session
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class DailyBudgetTest {
    private val tz = TimeZone.of("UTC")


    @Test
    fun budgetCalculation(
    ) {
        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(), map(
            Range.all<Instant>() to 4.hours
        ))

        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
            "1. 13:00 - 1. 14:00"
        ), map(
            b("1. 00:00",               4.hours),
            v("1. 00:00 - 2. 00:00",    3.hours),
            a("2. 00:00",               4.hours)
        ))

        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
            "1. 13:00 - 1. 14:00",
            "1. 14:00 - 1. 15:00",
            "1. 23:00 - 1. 23:59"
        ), map(
            b("1. 00:00",               4.hours),
            v("1. 00:00 - 2. 00:00",    1.hours + 1.minutes),
            a("2. 00:00",               4.hours)
        ))

        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
            "1. 13:00 - 1. 14:00",
            "1. 14:00 - 1. 15:00",
            "1. 23:00 - 1. 23:59",
            "4. 20:00 - 4. 23:59",
            "6. 19:00 - 6. 23:59",
        ), map(
            b("1. 00:00",               4.hours),
            v("1. 00:00 - 2. 00:00",    1.hours + 1.minutes),
            v("2. 00:00 - 4. 00:00",    4.hours),
            v("4. 00:00 - 5. 00:00",    1.minutes),
            v("5. 00:00 - 6. 00:00",    4.hours),
            v("6. 00:00 - 7. 00:00",    0.hours),
            a("7. 00:00",               4.hours)
        ))
        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
            "7. 23:00 - 8. 02:00", // 1h + 2h
            "8. 22:00 - 9. 02:00", // 2h + 2h
        ), map(
            b("7. 00:00",               4.hours),
            v("7. 00:00 - 8. 00:00",    3.hours),
            v("8. 00:00 - 9. 00:00",    0.hours),
            v("9. 00:00 - 10. 00:00",   2.hours),
            a("10. 00:00",              4.hours)
        ))

        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
            "1. 13:00 - 1. 14:00", // 1h
            "1. 14:00 - 1. 15:00", // 1h
            "1. 23:00 - 1. 23:59", // 1h
            "4. 20:00 - 4. 23:59", // 3h 59m
            "6. 19:00 - 6. 23:59", // 4h 59m
            "7. 23:00 - 8. 02:00", // 1h + 2h
            "8. 22:00 - 9. 02:00", // 2h + 2h
        ), map(
            b("1. 00:00",               4.hours),
            v("1. 00:00 - 2. 00:00",    1.hours + 1.minutes),
            v("2. 00:00 - 4. 00:00",    4.hours),
            v("4. 00:00 - 5. 00:00",    1.minutes),
            v("5. 00:00 - 6. 00:00",    4.hours),
            v("6. 00:00 - 7. 00:00",    0.hours),
            v("7. 00:00 - 8. 00:00",    3.hours),
            v("8. 00:00 - 9. 00:00",    0.hours),
            v("9. 00:00 - 10. 00:00",   2.hours),
            a("10. 00:00",              4.hours)
        ))

        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
            "1. 13:00 - 1. 14:00", // 1h
            "2. 14:00 - 2. 15:00", // 1h
            "8. 22:00 - 9. 02:00", // 2h + 2h
        ), map(
            b("1. 00:00",               4.hours),
            v("1. 00:00 - 2. 00:00",    3.hours),
            v("2. 00:00 - 3. 00:00",    3.hours),
            v("3. 00:00 - 8. 00:00",    4.hours),
            v("8. 00:00 - 9. 00:00",   2.hours),
            v("9. 00:00 - 10. 00:00",   2.hours),
            a("10. 00:00",              4.hours)
        ))

    }

    private fun b(inst: String, duration: Duration): Pair<Range<Instant>, Duration> {
        return Range.lessThan(instant(inst)) to duration
    }

    private fun a(inst: String, duration: Duration): Pair<Range<Instant>, Duration> {
        return Range.atLeast(instant(inst)) to duration
    }

    private fun v(inst: String, duration: Duration): Pair<Range<Instant>, Duration> {
        val (start, stop) = inst.split(" - ")
        return Range.closedOpen(instant(start), instant(stop)) to duration
    }

    private fun map(vararg map: Pair<Range<Instant>, Duration>): TreeRangeMap<Instant, Duration>{
        return TreeRangeMap.create<Instant, Duration>().apply {
            map.forEach { (range, duration) ->
                put(range, duration)
            }
        }
    }
    
    private fun Budget.assertCorrectDailyBudget(sessions: List<String>, expected: RangeMap<Instant, Duration>){
        val exp = expected.asMapOfRanges()
        val actual = request(sessions.map { session(it) }).asMapOfRanges()
        assertThat(actual).containsExactlyEntriesIn(exp)
    }
    
    private fun session(startStop: String): Session {
        val (start, stop) = startStop.split(" - ")
        return session(start, stop)
    }

    private fun session(start: String, stop: String): Session {
        return Session(
            instant(start),
            instant(stop),
        )
    }
    private fun instant(str: String): Instant {
        var (day, hour) = str.split(" ")
        day = if (day.length < 3) "0$day" else day
        return LocalDate.parse("2007-12-${day.dropLast(1)}").atTime(LocalTime.parse(hour)).toInstant(tz)
    }
}