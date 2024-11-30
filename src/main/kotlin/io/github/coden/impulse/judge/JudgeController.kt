package io.github.coden.impulse.judge

import io.github.coden.impulse.judge.service.JudgeService
import io.github.coden.impulse.judge.service.Verdict
import io.github.coden.impulse.judge.telegram.Notifier
import io.github.coden.wellpass.api.CheckIns
import io.github.coden.wellpass.api.Wellpass
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@RestController
class JudgeController(
    private val notifier: Notifier,
    private val service: JudgeService,
    private val wellpass: Wellpass
) {

    val singleThreadExecutor = Executors.newSingleThreadExecutor()

    @GetMapping("/")
    fun index(): String {
        return "Hi."
    }

    @GetMapping("/check")
    fun check(@RequestParam(required = false, defaultValue = "false") hard: Boolean): Mono<ResponseEntity<Verdict>> {
        return service
            .check(hard)
            .map {
                singleThreadExecutor.submit { notifyNextLockdown(it.nextChange) }
                if (it.guilty){
                    ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(it)
                }
                else ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(it)
            }
    }
    val dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss, 'on' dd.MMMM yyyy")
    private fun notifyNextLockdown(nextChange: LocalDateTime){
        val untilLockdown = Duration.between(LocalDateTime.now(), nextChange)
        println("Next lockdown is at ${nextChange.format(dateFormat)}, Remains: ${untilLockdown.toHours()} hours")
        if (untilLockdown <= 2.2.days.toJavaDuration() && untilLockdown.isPositive && ((untilLockdown.toHoursPart()+1) % 8 == 0)){
            notifier.notify("⚠\uFE0F Warning! Next lockdown is at ${nextChange.format(dateFormat)}\nRemains: ${untilLockdown.toHours()} hours")
        }
        if (untilLockdown.toHoursPart() == 0){
            notifier.notify("⛔ Internet disabled, rule violated, lockdown!")
        }
    }

    @GetMapping("/wellpass")
    fun wellpass(): Mono<CheckIns> {
        return wellpass
            .checkins(LocalDate.now())
            .timeout(Duration.ofSeconds(60))
    }
}