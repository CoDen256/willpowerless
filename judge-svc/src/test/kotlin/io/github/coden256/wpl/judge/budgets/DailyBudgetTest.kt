package io.github.coden256.wpl.judge.budgets

import com.google.common.collect.Range
import io.github.coden256.wpl.judge.core.Session
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class BudgetCalculatorTest {
    private val calculator = DailyBudget(budgetAmount)

    companion object {
        private val budgetAmount = 4.hours
        @JvmStatic
        fun nonOverlappingSessionsProvider(): Stream<TestCase> = Stream.of(
            // Case 1: No sessions - full budget available
            TestCase(
                sessions = emptyList(),
                expected = mapOf(
                    Range.atLeast(Instant.DISTANT_PAST) to budgetAmount
                )
            ),

            // Case 2: Single session within budget
            TestCase(
                sessions = listOf(
                    Session(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(1.hours))
                ),
                expected = mapOf(
                    Range.closedOpen(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(1.hours))
                            to budgetAmount.minus(1.hours),
                    Range.atLeast(Instant.DISTANT_PAST.plus(1.hours))
                            to budgetAmount.minus(1.hours)
                )
            ),

            // Case 3: Multiple non-overlapping sessions
            TestCase(
                sessions = listOf(
                    Session(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(3.minutes)),
                    Session(Instant.DISTANT_PAST.plus(1.hours),
                        Instant.DISTANT_PAST.plus(2.hours))
                ),
                expected = mapOf(
                    Range.closedOpen(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(3.minutes))
                            to budgetAmount.minus(3.minutes),
                    Range.closedOpen(Instant.DISTANT_PAST.plus(3.minutes),
                        Instant.DISTANT_PAST.plus(1.hours))
                            to budgetAmount.minus(3.minutes),
                    Range.closedOpen(Instant.DISTANT_PAST.plus(1.hours),
                        Instant.DISTANT_PAST.plus(2.hours))
                            to budgetAmount.minus(1.hours.plus(3.minutes)),
                    Range.atLeast(Instant.DISTANT_PAST.plus(2.hours))
                            to budgetAmount.minus(1.hours.plus(3.minutes))
                )
            ),

            // Case 4: Exact budget consumption
            TestCase(
                sessions = listOf(
                    Session(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(budgetAmount))
                ),
                expected = mapOf(
                    Range.closedOpen(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(budgetAmount))
                            to Duration.ZERO,
                    Range.atLeast(Instant.DISTANT_PAST.plus(budgetAmount))
                            to Duration.ZERO
                )
            ),

            // Case 5: Multiple sessions with gaps
            TestCase(
                sessions = listOf(
                    Session(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(1.hours)),
                    Session(Instant.DISTANT_PAST.plus(2.hours),
                        Instant.DISTANT_PAST.plus(3.hours))
                ),
                expected = mapOf(
                    Range.closedOpen(Instant.DISTANT_PAST, Instant.DISTANT_PAST.plus(1.hours))
                            to budgetAmount.minus(1.hours),
                    Range.closedOpen(Instant.DISTANT_PAST.plus(1.hours),
                        Instant.DISTANT_PAST.plus(2.hours))
                            to budgetAmount.minus(1.hours),
                    Range.closedOpen(Instant.DISTANT_PAST.plus(2.hours),
                        Instant.DISTANT_PAST.plus(3.hours))
                            to budgetAmount.minus(2.hours),
                    Range.atLeast(Instant.DISTANT_PAST.plus(3.hours))
                            to budgetAmount.minus(2.hours)
                )
            )
        )
    }

    @ParameterizedTest
    @MethodSource("nonOverlappingSessionsProvider")
    fun `calculateRemainingTime with non-overlapping sessions`(testCase: TestCase) {
        // When
        val result = calculator.request(testCase.sessions)

        // Then
        assertEquals(testCase.expected.size, result.asMapOfRanges().size,
            "Number of ranges should match")

        testCase.expected.forEach { (expectedRange, expectedDuration) ->
            val actualDuration = result.get(expectedRange.lowerEndpoint())
            assertEquals(expectedDuration, actualDuration,
                "Remaining duration for range $expectedRange should match")
        }
    }

    data class TestCase(
        val sessions: List<Session>,
        val expected: Map<Range<Instant>, Duration>
    )
}