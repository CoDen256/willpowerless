package io.github.coden256.wpl.judge.web

import io.github.coden256.wpl.judge.core.Judge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.RulingTree
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
    fun getLaws(): ResponseEntity<Map<Int, Law>> {
        return judge
            .laws()
            .associateBy { it.priority }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{law}")
    fun getLaw(@PathVariable law: Int): Mono<ResponseEntity<RulingTree>> {
        return judge
            .laws()
            .firstOrNull { it.priority == law }
            ?.verify()
            ?.map {
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(it)
            }
            ?: Mono.just(ResponseEntity.notFound().build())
    }
}

