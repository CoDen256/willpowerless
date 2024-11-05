package io.github.coden.impulse.judge

import io.github.coden.utils.randomPronouncable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JudgeController {

    @GetMapping("/check")
    fun index(): String {
        return "Hi."
    }
    @GetMapping("/check")
    fun stuff(): String{
        return randomPronouncable(5, 10) + ("Hello World! ")
    }
}