package io.github.coden256.wpl.judge.web

import com.fasterxml.jackson.databind.JsonNode
import io.github.coden256.wpl.judge.core.Judge
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Mono

//@RestController
@RequestMapping("/rulings")
class RulingController(
    private val judge: Judge
) {

    @GetMapping("/**")
    fun getRuling(request: ServerHttpRequest): Mono<ResponseEntity<JsonNode>> {
        return judge
            .verify()
            .map { tree ->
                val path = extractPathFromRequest(request)
                val result = tree.get(path)
                if (result.isNull) {
                    ResponseEntity.notFound().build()
                } else {
                    ResponseEntity.ok(result)
                }
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
}

