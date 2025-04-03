package io.github.coden256.wpl.judge.rules

import io.github.coden256.wpl.judge.api.Match
import io.github.coden256.wpl.judge.api.Match.Companion.asMatch
import io.github.coden256.wpl.judge.api.Rule
import java.time.DayOfWeek.*
import java.time.LocalDateTime
import java.time.LocalTime

class TimeRule: Rule<LocalDateTime> {

    private val workTimeFrom = LocalTime.of(8, 0)
    private val workTimeTo = LocalTime.of(16, 0)
    private val homeWorkDays = listOf(
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY
    )
    private val schedule = listOf(
        MONDAY, TUESDAY, WEDNESDAY, FRIDAY, SATURDAY
    )

    override fun test(entity: LocalDateTime): Match = rule().invoke(entity)

    private fun rule() = isWork() or isScheduled()

    private fun isWork() = isWorkTime() and isWorkDay()

    private fun isWorkTime() = Rule<LocalDateTime> {
        val time = it.toLocalTime()
         (!time.isBefore(workTimeFrom) && !time.isAfter(workTimeTo))
             .asMatch()
             .onFail("⛔ Not a work time ($workTimeFrom-$workTimeTo)")
             .onSuccess("✅ Work time ($workTimeTo-$workTimeFrom)")
    }

    private fun isWorkDay() = Rule<LocalDateTime> {
        (it.dayOfWeek in homeWorkDays)
            .asMatch()
            .onFail("⛔ Not a home office work day $homeWorkDays")
            .onSuccess("✅ Home office work day $homeWorkDays")
    }

    private fun isScheduled() = Rule<LocalDateTime> {
        (it.dayOfWeek in schedule)
            .asMatch()
            .onFail("⛔ Not in schedule $schedule")
            .onSuccess("✅ In schedule $schedule")
    }
}