package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.verifiers.Verifier

class JudgeLaw(
    private val verifiers: List<Verifier<*>>,
    private val name: String,
    private val description: String?,
    private val rulings: List<LawRuling>,
    private val priority: Int
)