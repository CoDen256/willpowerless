package io.github.coden256.wpl.judge

import io.github.coden256.wellpass.config.WellpassConfiguration
import io.github.coden256.wpl.judge.components.LawAggregatingJudge
import io.github.coden256.wpl.judge.components.LocalTimeRangeConverter
import io.github.coden256.wpl.judge.components.VerifierBeanByConfigReplicator
import io.github.coden256.wpl.judge.config.RuleConverter
import io.github.coden256.wpl.judge.verifiers.CalenderAbsenceVerifier
import io.github.coden256.wpl.judge.verifiers.ScheduleVerifier
import io.github.coden256.wpl.judge.verifiers.WellpassVerifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(*[VerifierBeanByConfigReplicator::class, WellpassVerifier::class,
    WellpassConfiguration::class, ScheduleVerifier::class, CalenderAbsenceVerifier::class,
    LocalTimeRangeConverter::class,
    RuleConverter::class, LawAggregatingJudge::class])
class WillpowerlessJudge

fun main(args: Array<String>) {
    runApplication<WillpowerlessJudge>(*args)
}
