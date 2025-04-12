package io.github.coden256.wpl.judge

import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.core.RulingTree
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RulingStream(private val laws: List<Law>): Logging {

    fun next(): Mono<RulingTree> {
        return Flux
            .merge(laws.map { it.stream() })
            .doOnSubscribe{ logger.info("Subscribed before next stream: $it") }
            .doOnNext{ logger.info("Merging: $it") }
            .scanWith({ RulingTree() }){ root: RulingTree, rulings: List<LawRuling> ->
                rulings.forEach { root.add(it.path, it.action) }
                root
            }
            .doOnNext{ logger.info("Scanned: $it") }
            .doOnSubscribe{ logger.info("Subscribed before next stream: $it") }
            .last()
            .doOnSubscribe{ logger.info("Subscribed after next stream: $it") }

    }
}