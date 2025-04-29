package io.github.coden256.wpl.judge

import io.github.coden256.wpl.judge.verifiers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

@SpringBootTest
class VerifierTest {


    @Autowired
    lateinit var env: Environment

    @Autowired
    lateinit var laws: MultipleLawsProperties

    @Autowired
    lateinit var verifierDefinitionProvider: VerifierDefinitionProvider

    @Test
    fun test(@Autowired list: List<Verifier<*>>?) {
        env
    }

    @Configuration
    @EnableConfigurationProperties(MultipleLawsProperties::class)
    @Import(*[VerifierBeanByConfigReplicator::class, TestVerifier::class])
    class Config {

    }
}