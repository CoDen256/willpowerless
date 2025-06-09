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
//        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(), map(
//            Range.all<Instant>() to 4.hours
//        ))

//        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
//            "1. 13:00 - 1. 14:00"
//        ), map(
//            b("1. 00:00",               4.hours),
//            v("1. 00:00 - 2. 00:00",    3.hours),
//            a("2. 00:00",               4.hours)
//        ))

//        DailyBudget(4.hours, tz).assertCorrectDailyBudget(listOf(
//            "1. 13:00 - 1. 14:00",
//            "1. 14:00 - 1. 15:00",
//            "1. 23:00 - 1. 23:59"
//        ), map(
//            b("1. 00:00",               4.hours),
//            v("1. 00:00 - 2. 00:00",    1.hours + 1.minutes),
//            a("2. 00:00",               4.hours)
//        ))

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
        val actual = request(sessions.map { session(it) })
        assertThat(actual.asMapOfRanges()).containsExactlyEntriesIn(
            expected.asMapOfRanges()
        )
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
        val (day, hour) = str.split(" ")
        return LocalDate.parse("2007-12-0${day.dropLast(1)}").atTime(LocalTime.parse(hour)).toInstant(tz)
    }
}