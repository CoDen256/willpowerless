package io.github.coden256.wpl.judge.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.File
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId

@ConfigurationProperties("budget")
data class BudgetProperties(
    val store: File,
    val start: LocalTime,
    val total: Duration,
    val maxDuration: Duration,
    val minDuration: Duration,
    val curve: Double,
    val tz: ZoneId
) {
}