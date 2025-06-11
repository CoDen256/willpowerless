package io.github.coden256.wpl.judge.web

import io.github.coden256.wpl.judge.core.OneShotBudget
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/budget")
class BudgetController(
    private val oneShotBudget: OneShotBudget
) {
    @PostMapping("/")
    fun request(): ResponseEntity<String> {
        oneShotBudget
            .request()
            .onSuccess { return ResponseEntity.ok("Budget created") }
            .onFailure { return ResponseEntity.badRequest().body(it.message) }

        throw AssertionError("unreachable")
    }

    @GetMapping("/")
    fun remaining(): ResponseEntity<String> {
        val remaining = oneShotBudget.remaining()
        return if (remaining.isPositive()) ResponseEntity.ok(remaining.toString()) else ResponseEntity.badRequest().body(remaining.toString())
    }
}