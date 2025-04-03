package io.github.coden256.wpl.judge.api

fun interface Rule<I>: (I) -> Match {
    override fun invoke(entity: I): Match = test(entity)

    fun test(entity: I): Match

    infix fun and(other: Rule<I>): Rule<I> {
        return Rule { this.test(it) and other.test(it) }
    }

    infix fun or(other: Rule<I>): Rule<I> {
        return Rule { this.test(it) or other.test(it) }
    }
}

data class Match(
    val allowed: Boolean,
    val reason: String,
) {
    infix fun and(other: Match): Match {
        return and(*arrayOf(other))
    }
    fun and(vararg other: Match): Match {
        val total = arrayOf(this).plus(other)
        return Match(total.all { it.allowed }, total.joinToString(" & ", "(", ")"){ it.reason })
    }

    infix fun or(other: Match): Match {
        return or(*arrayOf(other))
    }

    fun or(vararg other: Match): Match {
        val total = arrayOf(this).plus(other)
        return Match(total.any { it.allowed }, total.joinToString(" | ", "(", ")") { it.reason })
    }

    fun onFail(msg: String): Match {
        return Match(allowed, if (allowed) reason else msg)
    }

    fun onSuccess(msg: String): Match {
        return Match(allowed, if (allowed) msg else reason)
    }

    companion object {
        fun Boolean.asMatch(): Match {
            return Match(this, if (this) "bad" else "ok")
        }
    }
}