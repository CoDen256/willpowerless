package io.github.coden256.wpl.judge

import org.springframework.core.env.Environment

inline fun <reified T> Environment.getListProperty(prefix: String): List<T> {
    val list = mutableListOf<T>()
    var index = 0
    while (true) {
        val value = tryGetProperty<T>("$prefix[$index]") ?: break
        list.add(value)
        index++
    }
    return list
}

inline fun <reified T> Environment.tryGetProperty(key: String): T? {
    return getProperty(key, T::class.java)
}