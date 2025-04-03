package io.github.coden256.wpl.judge.rules

import io.github.coden256.wpl.judge.api.Match
import io.github.coden256.wpl.judge.api.Match.Companion.asMatch
import io.github.coden256.wpl.judge.api.Rule

class HardCheckRule: Rule<Boolean> {
    override fun test(isHardCheck: Boolean): Match {
        return (isHardCheck)
            .asMatch()
            .onFail("⛔ Not hard check")
            .onSuccess("✅ Hard check")
    }
}