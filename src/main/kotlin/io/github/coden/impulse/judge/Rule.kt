package io.github.coden.impulse.judge

interface Rule<I> {
    fun test(entity: I): Match
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