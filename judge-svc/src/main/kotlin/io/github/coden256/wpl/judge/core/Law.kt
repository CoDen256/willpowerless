package io.github.coden256.wpl.judge.core

import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface Law {
    fun rulings(): List<LawRuling>
    fun verify(): Mono<Verdict>
    fun name(): String
}

data class Verdict(
    val rulings: List<LawRuling>,
    val enabled: Boolean,
    val expires: LocalDateTime,
    val reason: String,
    val law: String,
)

data class LawRuling(
    val path: String,
    val action: Ruling,
) {
    fun withReason(prefix: String, reason: String): LawRuling{
        return LawRuling(path, Ruling(action.action, prefix + action.reason + reason ))
    }
}
