package io.github.coden.impulse.judge

import io.github.coden.utils.randomPronouncable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JudgeController {
    @GetMapping("/check")
    fun stuff(): String{
        return ("Hello World! ") + randomPronouncable(5, 10)
    }
}