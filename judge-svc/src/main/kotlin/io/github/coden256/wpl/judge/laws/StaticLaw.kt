package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.config.RulingRegistry
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.core.Verdict
import org.springframework.core.env.Environment
import reactor.core.publisher.Mono
import java.time.LocalDateTime

open class StaticLaw(
    protected val name: String,
    env: Environment,
    protected val registry: RulingRegistry,
) : Law {

    protected val config: Cfg = parseCfg(env)

    data class Cfg(
        val description: String,
        val rulings: List<String>,
        val enabled: Boolean = true
    )

    private fun parseCfg(env: Environment): Cfg {
        return Cfg(
            description = env.getProperty("laws.$name.description")!!,
            rulings = env.getListProperty<String>("laws.$name.rulings"),
            enabled = env.getProperty("laws.$name.enabled", Boolean::class.java) ?: true
        )
    }

    private inline fun <reified T> Environment.getListProperty(prefix: String): List<T> {
        val list = mutableListOf<T>()
        var index = 0
        while (true) {
            val value = getProperty("$prefix[$index]", T::class.java) ?: break
            list.add(value)
            index++
        }
        return list
    }

    override fun rulings(): List<LawRuling> = registry.getRules(config.rulings)

    override fun verify(): Mono<Verdict> {
        return Mono.just(
            Verdict(
                rulings(),
                enabled = true,
                expires = LocalDateTime.MAX,
                reason = config.description,
                law = name
            )
        )
    }

}