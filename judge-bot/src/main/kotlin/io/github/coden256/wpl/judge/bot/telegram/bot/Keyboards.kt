package io.github.coden256.wpl.judge.bot.telegram.bot

import io.github.coden256.telegram.keyboard.Keyboard
import io.github.coden256.telegram.keyboard.KeyboardButton
import io.github.coden256.telegram.keyboard.keyboard

fun withRequestButtons(): Keyboard {
    return keyboard {
        row { b(GET_BUDGET); b(REQUEST_BUDGET) }
    }
}

val REQUEST_BUDGET = KeyboardButton("Request", "REQUEST")
val GET_BUDGET = KeyboardButton("Check", "REMAINING")