package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.config.RulingRegistry
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.core.Verdict
import io.github.coden256.wpl.judge.laws.Cfg.Companion.parseCfg
import io.github.coden256.wpl.judge.laws.Schedule.Companion.parseSchedule
import org.apache.commons.lang3.Range
import org.springframework.core.env.Environment
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

open class StaticLaw(
    protected val name: String,
    env: Environment,
    protected val registry: RulingRegistry,
) : Law {

    protected val config: Cfg = env.parseCfg(name)

    override fun rulings(): List<LawRuling> = registry.getRules(config.rulings)

    override fun verify(): Mono<Verdict> {
        val current = LocalDateTime.now()
        return Mono.just(
            Verdict(
                rulings(),
                enabled = config.schedule.any { it.matches(current) } || config.schedule.isEmpty(),
                expires = LocalDateTime.MAX,
                reason = config.description,
                law = name
            )
        )
    }

    override fun name(): String {
        return name
    }
}

data class Cfg(
    val description: String,
    val rulings: List<String>,
    val enabled: Boolean = true,
    val schedule: List<Schedule> = emptyList()
) {
    companion object {
        fun Environment.parseCfg(name: String): Cfg {
            return Cfg(
                description = getProperty("laws.$name.description")!!,
                rulings = getListProperty<String>("laws.$name.rulings"),
                enabled = getProperty("laws.$name.enabled", Boolean::class.java) ?: true,
                schedule = getListProperty<Schedule>("laws.$name.schedule")
            )
        }
    }
}

data class Schedule(
    val negate: Boolean = false,
    val timeRange: Range<LocalTime> = Range.of(LocalTime.MIN, LocalTime.MAX),
    val daysOfWeek: List<DayOfWeek>,
) {
    companion object {
        fun Environment.parseSchedule(prefix: String): Schedule? {
            val negate = getProperty("$prefix.negate", Boolean::class.java)
            val timeRange = getProperty("$prefix.timeRange")
            val list = getListProperty<String>("$prefix.daysOfWeek")
            if (list.isEmpty() && timeRange == null && negate == null) return null
            return Schedule(
                negate ?: false,
                timeRange?.let { parseTimeRange(it) } ?: Range.of(LocalTime.MIN, LocalTime.MAX),
                list.map { DayOfWeek.valueOf(it) }
            )
        }

        private fun parseTimeRange(range: String): Range<LocalTime> {
            val (start, end) = range
                .split("-")
                .map { it.trim() }
                .map { LocalTime.parse(it) }
            return Range.of(start, end)
        }
    }

    fun matches(current: LocalDateTime): Boolean {
        val match = timeRange.contains(current.toLocalTime()) && daysOfWeek.contains(current.dayOfWeek)
        return (match && !negate) || (!match && negate)
    }

    fun <T> NegatableProperty<T>.matches(matching: (T) -> Boolean): Boolean {
        val match = matching(value)
        return (match && !negate) || (!match && negate)
    }

}

data class NegatableProperty<T>(val value: T, val negate: Boolean) {
    companion object {
        fun <T> parse(property: String?, restParsing: (String) -> T): NegatableProperty<T>? {
            if (property == null) return null
            val negated = property.trimStart().startsWith("!")
            val new = property.trimStart().removePrefix("!").trimStart()
            return NegatableProperty(restParsing(new), negated)
        }
    }
}

inline fun <reified T> Environment.getListProperty(prefix: String): List<T> {
    val list = mutableListOf<T>()
    var index = 0
    while (true) {
        val value = tryGetProperty<T>("$prefix[$index]") ?: break
        list.add(value)
        index++
    }
    return list
}

inline fun <reified T> Environment.tryGetProperty(key: String): T? {
    if (T::class == Schedule::class) {
        return this.parseSchedule(key) as T?
    }
    return getProperty(key, T::class.java)
}