package io.github.coden256.wpl.judge.web

import io.github.coden256.wpl.judge.config.RulingSet
import io.github.coden256.wpl.judge.core.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/laws")
class LawController(
    private val judge: Judge
) {

    @GetMapping("/")
    fun getLaws(): ResponseEntity<Map<Int, SimpleLaw>> {
        return judge
            .laws()
            .map { it.simple() }
            .filter { it.enabled }
            .associateBy { it.priority }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{law}")
    fun getLaw(@PathVariable law: Int): Mono<ResponseEntity<MutableMap<String, Ruling>>> {
        return judge
            .laws()
            .firstOrNull { it.priority == law }
            ?.verify()
            ?.map {
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(it.added)
            }
            ?: Mono.just(ResponseEntity.notFound().build())
    }

    private fun Law.simple(): SimpleLaw {
        return SimpleLaw(
            name = name,
            verifiers = verifiers.mapNotNull { it::class.simpleName },
            rulingSet = rulingSet,
            description = description,
            priority = priority,
            enabled = enabled,
        )
    }

    data class SimpleLaw(
        val name: String,
        val verifiers: List<String>,
        val rulingSet: RulingSet,
        val description: String,
        val priority: Int,
        val enabled: Boolean
    )
}

