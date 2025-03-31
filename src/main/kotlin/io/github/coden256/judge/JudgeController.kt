package io.github.coden256.judge

import io.github.coden256.judge.api.Rule
import io.github.coden.wellpass.api.CheckIns
import io.github.coden.wellpass.api.Wellpass
import io.github.coden256.calendar.api.Calendar
import io.github.coden256.calendar.api.Absence
import io.github.coden256.judge.rules.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.hours

@RestController
class JudgeController(
    private val wellpass: Wellpass,
    private val calendar: Calendar
) {
    val isGymVisited: Rule<CheckIns> = WellpassRule()
    val isWithinSchedule: Rule<LocalDateTime> = TimeRule()
    val isSickOrVacation: Rule<List<Absence>> = AbsenceRule()
    val isHardCheck: Rule<Boolean> = HardCheckRule()

    @GetMapping("/")
    fun index(): String {
        return "Hi."
    }

    @GetMapping("/check")
    fun check(@RequestParam(required = false, defaultValue = "false") hard: Boolean): Mono<ResponseEntity<Verdict>> {
        return wellpass
            .checkins(LocalDate.now().minusMonths(4), LocalDate.now())
            .timeout(Duration.ofSeconds(60))
            .map {

                val match = (isGymVisited(it).and(isWithinSchedule(LocalDateTime.now())))
                    .or(isSickOrVacation(getLongAbsences()), isHardCheck(hard))

                Verdict(
                    match.allowed,
                    match.reason,
                    it.copy(checkIns = it.checkIns.sortedByDescending { it.checkInDate })
                )
            }
            .map {
                if (!it.innocent) {
                    ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(it)
                } else ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(it)
            }
    }

    @GetMapping("/wellpass")
    fun wellpass(): Mono<CheckIns> {
        return wellpass
            .checkins(LocalDate.now())
            .timeout(Duration.ofSeconds(60))
    }

    @GetMapping("/absence")
    fun absence(): Mono<List<Absence>> {
        return Mono.fromSupplier { getLongAbsences() }
    }

    private fun getLongAbsences(): List<Absence> {
        return try {
            calendar.absences()
                .filter { it.start.isBefore(LocalDateTime.now()) }
                .filter { it.duration() >= 23.9.hours }
                .map {
                    when (it.end.dayOfWeek) {
                        DayOfWeek.THURSDAY -> it.copy(end = it.end.plusDays(3))
                        DayOfWeek.FRIDAY -> it.copy(end = it.end.plusDays(2))
                        DayOfWeek.SATURDAY -> it.copy(end = it.end.plusDays(1))
                        else -> it
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    data class Verdict(
        val innocent: Boolean,
        val reason: String,
        val evidence: Any?,
    )
}