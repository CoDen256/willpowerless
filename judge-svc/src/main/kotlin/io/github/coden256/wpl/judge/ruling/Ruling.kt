package io.github.coden256.wpl.judge.ruling

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode


class RulingNode(
    path: List<String> = emptyList(),
    ruling: Action? = null
) : ObjectNode(JsonNodeFactory.instance) {

    init {
        if (path.isEmpty() && ruling != null) {
            put("ruling", ruling.toString())
        } else if (path.isNotEmpty()) {
            replace(path.first(), RulingNode(path.drop(1), ruling))
        }
    }

    fun add(path: String, action: Action) {
        val segments = path.split("/").filter { it.isNotEmpty() }
        val head = segments.first()
        if (!has(head)) {
            replace(head, RulingNode(segments.drop(1), action))
        }else{

        }
    }

    fun json(): JsonNode {
        return this
    }
}

data class Path(val path: String) {
    val segments = path.split("/").filter { it.isNotEmpty() }

    init {
        if (!path.startsWith("/")) throw InvalidPathRootException("Invalid path $path, not starting with '/'")
    }

    fun head(): String {
        return segments[0]
    }

    fun tail(): String {
        return segments.drop(1).joinToString("/")
    }
}

class Ruling(
    val action: Action,
    val path: String
) {


    override fun toString(): String {
        return path
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ruling

        if (action != other.action) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}


class InvalidPathRootException(msg: String) : RuntimeException(msg)
class InvalidPathException(msg: String) : RuntimeException(msg)
class InvalidSubRulingException(msg: String) : RuntimeException(msg)

enum class Action() {
    BLOCK, ALLOW, FORCE
}