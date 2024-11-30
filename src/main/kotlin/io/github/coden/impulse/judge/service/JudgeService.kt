package io.github.coden.impulse.judge.service

import reactor.core.publisher.Mono

interface JudgeService {
    fun check(hard: Boolean): Mono<Verdict>
}