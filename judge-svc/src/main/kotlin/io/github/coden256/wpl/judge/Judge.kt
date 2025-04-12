package io.github.coden256.wpl.judge

import io.github.coden256.wpl.judge.core.RulingTree
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

interface Judge{
    fun rulings(): Mono<RulingTree>
}


@Component
class StreamJudge(
    private val stream: RulingStream
): Judge, Logging{
    override fun rulings(): Mono<RulingTree> {
        return stream.next().doOnNext { logger.info("Judge: $it") }
    }
}


