package io.github.coden256.wpl.judge.laws

//
//class WellpassRule : Rule<CheckIns> {
//
//    private val chillDuration = 5.days
//
//    override fun test(entity: CheckIns): Match {
//        val checkIns = entity.checkIns
//        val last = checkIns.maxByOrNull { it.checkInDate }
//        return checkIns
//            .filter { filterGyms(it) }
//            .any { it.checkInDate.isAfter(LocalDateTime.now().minus(chillDuration.toJavaDuration())) }
//            .asMatch()
//            .onFail("⛔ Last checkin was more than ${chillDuration} ago:  ${last?.name} on ${last?.checkInDate}")
//            .onSuccess("✅ Gym alright")
//    }
//
//    private fun filterGyms(it: CheckIn) =
//        it.name.lowercase().contains("yoga|boulder|fitness first|kletter|fit/one".toRegex()) &&
//                it.name.lowercase().contains("leipzig|plagwitz".toRegex())
//}