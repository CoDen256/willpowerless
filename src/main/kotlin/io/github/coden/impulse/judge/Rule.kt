package io.github.coden.impulse.judge

import java.time.LocalDateTime

interface Rule<I> {
    fun test(entity: I): Match
}

interface TimeDependentRule<I>: Rule<I>{
    fun nextAllowed(entity: I): LocalDateTime
    fun nextDisallowed(entity: I): LocalDateTime
}

data class Match(
    val allowed: Boolean,
    val reason: String,
) {
    companion object {
        fun Boolean.ifFailed(failed: String, allowed: String = "ok"): Match {
            return Match(this, if (this) allowed else failed)
        }
    }
}