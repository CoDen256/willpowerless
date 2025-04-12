package io.github.coden256.wpl.judge.ruling

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode


class RulingNode {
    private val children = mutableMapOf<String, RulingNode>()
    private var ruling: Ruling? = null

    fun add(path: String, action: Action) {
        val parts = path.split('/').filter { it.isNotEmpty() }
        add(parts, Ruling(action))
    }

    fun add(path: String, ruling: Ruling) {
        val parts = path.split('/').filter { it.isNotEmpty() }
        add(parts, ruling)
    }

    fun get(path: String, mapper: ObjectMapper = ObjectMapper()): JsonNode {
        val parts = path.split('/').filter { it.isNotEmpty() }
        return get(parts, mapper)
    }

    private fun get(parts: List<String>, mapper: ObjectMapper): JsonNode {
        if (parts.isEmpty()) {
            // Base case: return the current node's JSON
            return this.json(mapper)
        }

        val first = parts.first()
        if (first == "ruling") {
            // Special case: requesting just the ruling value
            return if (parts.size == 1) {
                ruling?.let { it.json(mapper) } ?: mapper.nullNode()
            } else {
                mapper.nullNode() // Invalid path like "/ruling/extra"
            }
        }

        val child = children[first] ?: return mapper.nullNode()
        return child.get(parts.drop(1), mapper)
    }

    private fun add(parts: List<String>, ruling: Ruling) {
        if (parts.isEmpty()) {
            this.ruling = ruling
            return
        }

        val first = parts.first()
        val child = children.getOrPut(first) { RulingNode() }
        child.add(parts.drop(1), ruling)
    }

    fun json(mapper: ObjectMapper = ObjectMapper()): JsonNode {
        val node = mapper.createObjectNode()

        ruling?.let {
            node.set<ObjectNode>("ruling", it.json(mapper))
        }

        children.forEach { (key, childNode) ->
            node.set<ObjectNode>(key, childNode.json(mapper))
        }

        return node
    }
}

data class Ruling(val action: Action, val reason: String? = null){

    fun json(mapper: ObjectMapper = ObjectMapper()): JsonNode {
        val node = mapper.createObjectNode()

        node.put("action", action.toString())

        reason?.let { node.put("reason", it) }

        return node
    }
}

enum class Action() {
    BLOCK, ALLOW, FORCE
}