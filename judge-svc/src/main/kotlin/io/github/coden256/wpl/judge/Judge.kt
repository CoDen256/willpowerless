package io.github.coden256.wpl.judge

import io.github.coden256.wpl.judge.core.Action
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.RulingTree
import io.github.coden256.wpl.judge.core.Verdict
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

interface Judge {
    fun rulings(): Mono<RulingTree>
}


@Component
class LawAggregatingJudge(
    private val laws: List<Law>
) : Judge, Logging {
    override fun rulings(): Mono<RulingTree> {
        val laws: List<Mono<Verdict>> = laws
            .map { it.verify() }

        return Mono.zip(laws) {
            val verdicts = it.filterIsInstance<Verdict>()
            val root = RulingTree()
            verdicts.forEach { verdict ->


                if (!verdict.enabled) {
                    logger.info { "Omitted: ${verdict.rulings}" }
                    return@forEach
                }
                verdict.rulings.forEach {
                    val marker = when (it.action.action) {
                        Action.BLOCK -> "⛔"
                        Action.ALLOW -> "\uD83D\uDFE1"
                        Action.FORCE -> "✅"
                    }
                    val new = it.withReason("-> ${verdict.law} -> $marker ${verdict.reason}")
                    logger.info { "[${verdict.law}]: $new" }
                    root.add(new.path, new.action)
                }
            }
            root
        }
    }
}


