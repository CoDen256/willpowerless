package io.github.coden256.wpl.judge.budgets

import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap
import io.github.coden256.wpl.judge.core.Budget
import io.github.coden256.wpl.judge.core.Session
import kotlinx.datetime.*
import kotlin.time.Duration

class DailyBudget(
    private val budget: Duration,
    private val tz: TimeZone = TimeZone.of("CET")
): Budget {

    override fun request(sessions: List<Session>): RangeMap<Instant, Duration> {
        val usage = calculateUsage(sessions, periods)
        val remaining = TreeRangeMap.create<Instant, Duration>()

        usage.asMapOfRanges().forEach { (range, usageDuration) ->
            remaining.put(range, budget.minus(usageDuration).coerceAtLeast(Duration.ZERO))
        }

        return remaining
    }

    fun calculateUsage(
        sessions: List<Session>,
        periods: List<Range<Instant>>
    ): RangeMap<Instant, Duration> {
        val rangeMap = TreeRangeMap.create<Instant, Duration>()

        // Initialize all periods with zero usage
        periods.forEach { range ->
            rangeMap.put(range, Duration.ZERO)
        }

        // Add each session's duration to the appropriate periods
        sessions.forEach { session ->
            val sessionRange = Range.closedOpen(session.start, session.stop)

            // Find all periods that overlap with this session
            val overlapping = rangeMap.subRangeMap(sessionRange).asMapOfRanges()

            if (overlapping.isEmpty()) {
                // Session spans multiple periods - we need to split it
                val containingRange = rangeMap.getEntry(session.start)?.key
                    ?: rangeMap.getEntry(session.stop)?.key
                    ?: throw IllegalStateException("No period found for session")

                val splitRanges = splitSessionAcrossPeriods(session, containingRange, rangeMap)
                splitRanges.forEach { (range, duration) ->
                    rangeMap.put(range, (rangeMap.get(range) ?: Duration.ZERO).plus(duration))
                }
            } else {
                overlapping.forEach { (range, currentDuration) ->
                    val overlapDuration = calculateOverlap(session, range)
                    rangeMap.put(range, currentDuration.plus(overlapDuration))
                }
            }
        }

        return rangeMap
    }

    private fun calculateOverlap(session: Session, range: Range<Instant>): Duration {
        val overlapStart = maxOf(session.start, range.lowerEndpoint())
        val overlapEnd = minOf(session.stop, range.upperEndpoint())
        return overlapStart - overlapEnd
    }

    private fun splitSessionAcrossPeriods(
        session: Session,
        containingRange: Range<Instant>,
        rangeMap: RangeMap<Instant, Duration>
    ): Map<Range<Instant>, Duration> {
        val result = mutableMapOf<Range<Instant>, Duration>()
        var remainingSession = session

        while (remainingSession.duration() > Duration.ZERO) {
            val currentRange = rangeMap.getEntry(remainingSession.start)?.key ?: containingRange
            val rangeEnd = currentRange.upperEndpoint()

            val splitPoint = minOf(remainingSession.stop, rangeEnd)
            val splitDuration = remainingSession.start - splitPoint

            result[currentRange] = splitDuration
            remainingSession = Session(splitPoint, remainingSession.stop)
        }

        return result
    }
}

fun main() {
    val tree: TreeRangeMap<Int, Duration> = TreeRangeMap.create<Int, Duration>()

    tree
}