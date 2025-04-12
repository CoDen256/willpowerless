package io.github.coden256.wpl.judge.core

import io.github.coden256.wpl.judge.api.Match
import java.time.LocalDateTime

interface Law {
    fun isEnabled(): Match
    fun expires(): LocalDateTime
    fun rulings(): List<LawRuling>
}

data class LawRuling(
    val path: String,
    val action: Ruling,
)

