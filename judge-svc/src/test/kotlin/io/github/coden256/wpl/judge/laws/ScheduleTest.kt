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
            NegatableProperty(Range.of(LocalTime.MIN, LocalTime.MAX), false),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 10, 0)))// sunday

        assertFalse( Schedule(
            NegatableProperty(Range.of(LocalTime.MIN, LocalTime.MAX), true),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 10, 0)))


        assertFalse( Schedule(
            NegatableProperty(Range.of(LocalTime.of(13,0), LocalTime.MAX), true),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 13, 10)))

        assertTrue( Schedule(
            NegatableProperty(Range.of(LocalTime.of(13,0), LocalTime.MAX), false),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 13,10)))

        assertFalse( Schedule(
            NegatableProperty(Range.of(LocalTime.of(13,0), LocalTime.MAX), false),
            listOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY)
        ).matches(LocalDateTime.of(2025, 4, 13, 13,10)))

    }
}