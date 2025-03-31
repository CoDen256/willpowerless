package io.github.coden256.judge.rules

import io.github.coden256.judge.api.Match
import io.github.coden256.judge.api.Match.Companion.asMatch
import io.github.coden256.judge.api.Rule

class HardCheckRule: Rule<Boolean> {
    override fun test(isHardCheck: Boolean): Match {
        return (isHardCheck)
            .asMatch()
            .onFail("❌ Not hard check")
            .onSuccess("✅ Hard check")
    }
}