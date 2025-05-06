package io.github.coden256.wpl.judge.core

import reactor.core.publisher.Mono

interface Judge {
    fun verify(): Mono<RulingTree>
    fun laws(): List<Law>
}