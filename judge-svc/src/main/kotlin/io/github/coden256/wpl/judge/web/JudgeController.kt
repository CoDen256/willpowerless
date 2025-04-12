package io.github.coden256.wpl.judge.web

import io.github.coden256.calendar.api.Calendar
import io.github.coden256.wellpass.api.Wellpass
import org.springframework.web.bind.annotation.RestController

@RestController
class JudgeController(
    private val wellpass: Wellpass,
    private val calendar: Calendar
) {
//    val isGymVisited: Rule<CheckIns> = WellpassRule()
//    val isWithinSchedule: Rule<LocalDateTime> = TimeRule()
//    val isSickOrVacation: Rule<List<Absence>> = RestorationLaw()
//    val isHardCheck: Rule<Boolean> = HardCheckRule()
//
//    @GetMapping("/")
//    fun index(): String {
//        return "Hi."
//    }
//
//    @GetMapping("/verdict")
//    fun check(@RequestParam(required = false, defaultValue = "false") hard: Boolean): Mono<ResponseEntity<Verdict>> {
//        return wellpass
//            .checkins(LocalDate.now().minusMonths(4), LocalDate.now())
//            .timeout(Duration.ofSeconds(60))
//            .map {
//
//                val match = (isGymVisited(it))
//                    .or(isSickOrVacation(getLongAbsences()), isHardCheck(hard))
//
//                Verdict(
//                    match.allowed,
//                    match.reason,
//                    it.copy(checkIns = it.checkIns.sortedByDescending { it.checkInDate })
//                )
//            }
//            .map {
//                if (!it.innocent) {
//                    ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(it)
//                } else ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(it)
//            }
//    }
//
//    @GetMapping("/wellpass")
//    fun wellpass(): Mono<CheckIns> {
//        return wellpass
//            .checkins(LocalDate.now())
//            .timeout(Duration.ofSeconds(60))
//    }
//
//    @GetMapping("/absence")
//    fun absence(): Mono<List<Absence>> {
//        return Mono.fromSupplier { getLongAbsences() }
//    }
//
//
//
//    data class Verdict(
//        val innocent: Boolean,
//        val reason: String,
//        val evidence: Any?,
//    )
}