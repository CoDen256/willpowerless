package io.github.coden256.wpl.judge

import io.github.coden256.wellpass.config.WellpassConfiguration
import io.github.coden256.wpl.judge.components.LawAggregatingJudge
import io.github.coden256.wpl.judge.config.JudgeConfiguration
import io.github.coden256.wpl.judge.config.MultipleLawProperties
import io.github.coden256.wpl.judge.config.MultipleRulingProperties
import io.github.coden256.wpl.judge.config.RuleConverter
import io.github.coden256.wpl.judge.core.Judge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierDefinitionProvider
import io.github.coden256.wpl.judge.verifiers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

@SpringBootTest
@Import(WellpassConfiguration::class)
class VerifierTest {


    @Autowired
    lateinit var env: Environment

    @Autowired
    lateinit var lawsProps: MultipleLawProperties

    @Autowired
    lateinit var verifierDefinitionProvider: VerifierDefinitionProvider

    @Autowired
    lateinit var laws: List<Law>

    @Autowired
    lateinit var judge: Judge

    @Test
    fun test(@Autowired list: List<Verifier<*>>?) {
        env
    }

    @Configuration
    @EnableConfigurationProperties(MultipleLawProperties::class,  MultipleRulingProperties::class)
    @Import(*[VerifierBeanByConfigReplicator::class, WellpassVerifier::class,
        WellpassConfiguration::class, ScheduleVerifier::class, CalenderAbsenceVerifier::class,
        LocalTimeRangeConverter::class,
        RuleConverter::class, JudgeConfiguration::class, LawAggregatingJudge::class])
    class Config
}