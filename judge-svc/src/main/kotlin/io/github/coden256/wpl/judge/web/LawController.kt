package io.github.coden256.wpl.judge.web

import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verdict
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

//@RestController
@RequestMapping("/laws")
class LawController(
    private val laws: List<Law>
) {

    @GetMapping("/")
    fun getLaws(): ResponseEntity<List<String>> {
        return laws
            .map { it.name() }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{law}")
    fun getLaw(@PathVariable law: String): Mono<ResponseEntity<Verdict>> {
        return laws
            .find { it.name() == law }
            ?.verify()
            ?.map {
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(it)
            }
            ?: Mono.just(ResponseEntity.notFound().build())
    }
}

