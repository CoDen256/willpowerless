package io.github.coden256.wpl.judge

import io.github.coden256.wpl.judge.core.RulingNode
import reactor.core.publisher.Flux

interface Judge{
    fun rulings(): Flux<RulingNode>
    fun emit()
}