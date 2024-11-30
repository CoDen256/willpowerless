package io.github.coden.impulse.judge.service

import java.time.LocalDateTime

data class Verdict(
        val guilty: Boolean,
        val reason: String,
        val evidence: Any?,
    val nextChange: LocalDateTime
    )