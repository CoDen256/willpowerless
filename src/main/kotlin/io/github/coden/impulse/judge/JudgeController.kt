package io.github.coden.impulse.judge

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JudgeController() {

    @GetMapping("/")
    fun index(): String {
        return "Hi."
    }
    @GetMapping("/check")
    fun check(): String{
        return ("Hello World! ")
    }
}