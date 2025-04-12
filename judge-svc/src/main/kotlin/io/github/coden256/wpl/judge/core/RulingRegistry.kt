package io.github.coden256.wpl.judge.core


interface RulingRegistry {
    fun registerRule(category: String, rule: LawRuling)
    fun getRules(category: String): List<LawRuling>
}
