package io.github.coden256.wpl.judge.web

import com.fasterxml.jackson.databind.JsonNode
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.core.RulingNode
import io.github.coden256.wpl.judge.laws.RestorationLaw
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rulings")
class RulingController(
    private val law: RestorationLaw
) {

    fun getNode(): RulingNode {
        return law.rulings().merge()
    }

    @GetMapping("/**")
    fun getRuling(request: ServerHttpRequest): ResponseEntity<JsonNode> {
        val path = extractPathFromRequest(request)
        val result = getNode().get(path)

        return if (result.isNull) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(result)
        }
    }

    private fun extractPathFromRequest(request: ServerHttpRequest): String {
        val fullPath = request.path

        // Remove the context path and /rulings prefix
        val pathAfterRulings = fullPath.value()
            .removePrefix("/rulings")
            .removePrefix("/")

        return "/$pathAfterRulings" // Ensure path starts with /
    }

    // Optional: Add endpoint to view the entire rulings tree
    @GetMapping
    fun getAllRulings(): ResponseEntity<JsonNode> {
        return ResponseEntity.ok(getNode().json())
    }
}

fun List<LawRuling>.merge(): RulingNode{
    val root = RulingNode()

    for (law in this) {
        root.add(law.path, law.action)
    }
    return root
}