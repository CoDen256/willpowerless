package io.github.coden256.wpl.judge.components

import io.github.coden256.wpl.judge.core.Judge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.RulingTree
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import kotlin.jvm.optionals.getOrNull


@Component
class LawAggregatingJudge(
    private val laws: List<Law>
) : Judge, Logging {
    override fun laws(): List<Law> {
        return laws
    }

    override fun verify(): Mono<RulingTree> {
        val trees: List<Mono<Optional<RulingTree>>> = laws
            .filter { it.enabled }
            .map {
                it.verify()
                    .map { Optional.of(it) }
                    .onErrorComplete()
                    .switchIfEmpty(Mono.just(Optional.empty()))
            }

        return Mono.zip(trees) {
            val root = RulingTree()

            it.filterIsInstance<Optional<RulingTree>>()
                .mapNotNull { it.getOrNull() }
                .forEach { tree ->
                    tree.added.forEach {
                        root.add(it.key, it.value)
                    }
                }
            return@zip root
        }
    }
}


