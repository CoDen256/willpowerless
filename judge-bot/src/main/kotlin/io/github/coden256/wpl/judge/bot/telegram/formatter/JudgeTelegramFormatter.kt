package io.github.coden256.wpl.judge.bot.telegram.formatter

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val MAX_CHARS_WITHOUT_BUTTON = 73

class JudgeTelegramFormatter : JudgeFormatter {

    private val default = DateTimeFormatter.ofPattern("d MMM HH:mm")

    private fun Instant.str(formatter: DateTimeFormatter): String {
        return formatter.format(this.atZone(ZoneId.of("CET")))
    }

    private fun Instant.str(pattern: String): String {
        return str(DateTimeFormatter.ofPattern(pattern))
    }



}