package io.github.coden256.judge

import io.github.coden.wellpass.api.CheckIn
import io.github.coden.wellpass.api.CheckIns
import io.github.coden256.judge.rules.WellpassRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WellpassRuleTest {

    @Test
    fun test() {
        // setup
        assertEquals(
            true, WellpassRule().test(CheckIns(listOf(
            CheckIn("KOSMOS Boulderhalle Leipzig", LocalDateTime.now())))).allowed)
        assertEquals(
            true, WellpassRule().test(CheckIns(listOf(
            CheckIn("Ute Stephan Yoga Studio Plagwitz", LocalDateTime.now())))).allowed)
        assertEquals(
            true, WellpassRule().test(CheckIns(listOf(
            CheckIn("Onyx Boulderspot Leipzig", LocalDateTime.now())))).allowed)
        assertEquals(
            true, WellpassRule().test(CheckIns(listOf(
            CheckIn("Fitness First Leipzig - Messehof", LocalDateTime.now())))).allowed)
        assertEquals(
            true, WellpassRule().test(CheckIns(listOf(
            CheckIn("Kletterhalle NO LIMIT Leipzig", LocalDateTime.now())))).allowed)
        assertEquals(
            true, WellpassRule().test(CheckIns(listOf(
            CheckIn("FIT/ONE Leipzig", LocalDateTime.now())))).allowed)

        assertEquals(false, WellpassRule().test(CheckIns(listOf(
            CheckIn("Vinya Loft Yogastudio Bremen", LocalDateTime.now())))).allowed)
        assertEquals(false, WellpassRule().test(CheckIns(listOf(
            CheckIn("Fitness", LocalDateTime.now())))).allowed)

        // exercise

        // verify

    }
}