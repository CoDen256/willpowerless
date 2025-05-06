package io.github.coden256.wpl.judge.components

import io.github.coden256.wpl.judge.core.Judge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.RulingTree
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class LawAggregatingJudge(
    private val laws: List<Law>
) : Judge, Logging {
    override fun laws(): List<Law> {
        return laws
    }

    override fun verify(): Mono<RulingTree> {
        val trees = laws.filter { it.enabled }.map { it.verify() }
        return Mono.zip(trees) {
            val root = RulingTree()

            it.filterIsInstance<RulingTree>()
                .forEach { tree ->
                    tree.added.forEach {
                        root.add(it.key, it.value)
                    }
                }
            return@zip root
        }
    }
}


