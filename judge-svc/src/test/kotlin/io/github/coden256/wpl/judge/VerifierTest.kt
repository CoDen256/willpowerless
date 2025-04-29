package io.github.coden256.wpl.judge

import io.github.coden256.wellpass.config.WellpassConfiguration
import io.github.coden256.wpl.judge.config.MultipleLawProperties
import io.github.coden256.wpl.judge.verifiers.CalenderAbsenceVerifier
import io.github.coden256.wpl.judge.verifiers.ScheduleVerifier
import io.github.coden256.wpl.judge.verifiers.api.Verifier
import io.github.coden256.wpl.judge.verifiers.api.VerifierBeanByConfigReplicator
import io.github.coden256.wpl.judge.verifiers.api.VerifierDefinitionProvider
import io.github.coden256.wpl.judge.verifiers.WellpassVerifier
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
    lateinit var laws: MultipleLawProperties

    @Autowired
    lateinit var verifierDefinitionProvider: VerifierDefinitionProvider

    @Test
    fun test(@Autowired list: List<Verifier<*>>?) {
        env
    }

    @Configuration
    @EnableConfigurationProperties(MultipleLawProperties::class)
    @Import(*[VerifierBeanByConfigReplicator::class, WellpassVerifier::class,
        WellpassConfiguration::class, ScheduleVerifier::class, CalenderAbsenceVerifier::class])
    class Config
}