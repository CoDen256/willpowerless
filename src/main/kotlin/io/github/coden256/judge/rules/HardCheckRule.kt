package io.github.coden256.judge.rules

import io.github.coden256.judge.api.Match
import io.github.coden256.judge.api.Match.Companion.asMatch
import io.github.coden256.judge.api.Rule

class HardCheckRule: Rule<Boolean> {
    override fun test(entity: Boolean): Match {
        return entity.asMatch("Not hard check")
    }
}