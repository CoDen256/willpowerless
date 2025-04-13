package io.github.coden256.wpl.judge.laws

import org.apache.commons.lang3.Range
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class ScheduleTest {

    @Test
    fun matches() {
        assertTrue( Schedule(
            false,
            Range.of(LocalTime.MIN, LocalTime.MAX),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 10, 0)))// sunday

        assertTrue( Schedule(
            true,
            Range.of(LocalTime.MIN, LocalTime.MAX),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 10, 0)))


        assertTrue( Schedule(
            true,
            Range.of(LocalTime.of(13,0), LocalTime.MAX),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 13, 10)))

        assertTrue( Schedule(
            false,
            Range.of(LocalTime.of(13,0), LocalTime.MAX),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 13,10)))

        assertFalse( Schedule(
            false,
            Range.of(LocalTime.of(13,0), LocalTime.MAX),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 13,10)))

    }
}