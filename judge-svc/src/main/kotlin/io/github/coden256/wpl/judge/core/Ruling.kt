package io.github.coden256.wpl.judge.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.Instant


class RulingTree {
    private val children = mutableMapOf<String, RulingTree>()
    private var ruling: Ruling? = null
    val added = mutableMapOf<String, Ruling>()


    override fun toString(): String {
        return json().toString()
    }

    fun add(path: String, action: Action) {
        val parts = path.split('/').filter { it.isNotEmpty() }
        add(parts, Ruling(action))
    }

    fun add(path: String, ruling: Ruling) {
        val parts = path.split('/').filter { it.isNotEmpty() }
        add(parts, ruling)
        added[path] = ruling
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

        children[first]?.let { exactMatchNode ->
            val result = exactMatchNode.get(parts.drop(1), mapper)
            if (!result.isNull) return result
        }

        // If no exact match, try wildcard match
        val wildcardMatch = children.entries
            .filter { (key, _) -> isWildcardMatch(key, first) }
            .maxByOrNull { (key, _) -> key.length } // Prefer most specific wildcard

        return wildcardMatch?.let { (_, node) ->
            node.get(parts.drop(1), mapper)
        } ?: mapper.nullNode()
    }

    private fun isWildcardMatch(pattern: String, input: String): Boolean {
        // Convert pattern to regex (escape everything except *)
        val regex = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
        return input.matches(Regex(regex))
    }

    private fun add(parts: List<String>, ruling: Ruling) {
        if (parts.isEmpty()) {
            this.ruling = this.ruling?.merge(ruling) ?: ruling
            return
        }

        val first = parts.first()
        val child = children.getOrPut(first) { RulingTree() }
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

data class Ruling(val action: Action,
                  val reason: String? = null,
                  val priority: Int = Int.MAX_VALUE,
                  val expiry: Instant = Instant.MAX,

                  ){

    fun json(mapper: ObjectMapper = ObjectMapper()): JsonNode {
        val node = mapper.createObjectNode()

        node.put("action", action.toString())
        node.put("priority", priority)
        node.put("expiry", expiry.toString())

        reason?.let { node.put("reason", it) }

        return node
    }

    fun merge(new: Ruling): Ruling{
        if (new.priority != priority) return listOf(this, new).minBy { it.priority }

        return when{
            this.action == new.action  -> Ruling(action, reason + " && " + new.reason)
            this.action == Action.ALLOW -> new
            new.action == Action.ALLOW -> this

            this.action == Action.BLOCK && new.action == Action.FORCE -> new
            this.action == Action.FORCE && new.action == Action.BLOCK -> this
            else -> this
        }
    }
}

enum class Action() {
    BLOCK, ALLOW, FORCE
}