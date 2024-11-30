package io.github.coden.impulse.judge.telegram

interface Notifier {
    fun notify(message: String)
}