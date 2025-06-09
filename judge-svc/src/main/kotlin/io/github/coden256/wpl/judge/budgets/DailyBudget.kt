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
): Budget {

    override fun request(sessions: List<Session>): RangeMap<Instant, Duration> {
        val rangeMap = TreeRangeMap.create<Instant, Duration>()

        if (sessions.isEmpty()) {
            // No sessions - full budget available from now
            rangeMap.put(Range.all(), budget)
            return rangeMap
        }

        // Create continuous timeline covering all sessions
        val timeline = createTimeline(sessions)

        // Calculate usage for each segment
        val usageMap = calculateUsagePerSegment(sessions, timeline)

        // Calculate remaining budget for each segment
        var remainingBudget = budget
        for (segment in timeline) {
            val usage = usageMap[segment] ?: Duration.ZERO
            remainingBudget = (budget - usage).coerceAtLeast(Duration.ZERO)
            rangeMap.put(segment, remainingBudget)
        }

        // Extend to future
        val lastSegment = timeline.last()
        rangeMap.put(Range.atLeast(lastSegment.upperEndpoint()), remainingBudget)

        return rangeMap
    }

    private fun createTimeline(sessions: List<Session>): List<Range<Instant>> {
        val points = sessions.flatMap { listOf(it.start, it.stop) }.toSortedSet()
        val timeline = mutableListOf<Range<Instant>>()

        var prev: Instant? = null
        for (point in points) {
            prev?.let { timeline.add(Range.closedOpen(it, point)) }
            prev = point
        }

        return timeline
    }

    private fun calculateUsagePerSegment(
        sessions: List<Session>,
        timeline: List<Range<Instant>>
    ): Map<Range<Instant>, Duration> {
        val usageMap = mutableMapOf<Range<Instant>, Duration>()

        for (segment in timeline) {
            var segmentUsage = Duration.ZERO

            for (session in sessions) {
                if (session.stop <= segment.lowerEndpoint()) continue
                if (session.start >= segment.upperEndpoint()) continue

                val overlapStart = maxOf(session.start, segment.lowerEndpoint())
                val overlapEnd = minOf(session.stop, segment.upperEndpoint())
                segmentUsage += overlapStart - overlapEnd
            }

            usageMap[segment] = segmentUsage
        }

        return usageMap
    }
}

fun main() {
    val tree: TreeRangeMap<Int, Duration> = TreeRangeMap.create<Int, Duration>()

    tree
}