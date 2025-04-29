package io.github.coden256.wpl.judge

import io.github.coden256.wpl.judge.verifiers.BeanReplicatorPostProcessor
import io.github.coden256.wpl.judge.verifiers.LawsConfigurationProperties
import io.github.coden256.wpl.judge.verifiers.TestVerifier
import io.github.coden256.wpl.judge.verifiers.Verifier
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
    lateinit var laws: LawsConfigurationProperties

    @Autowired
    lateinit var list: List<Verifier<*>>
    @Test
    fun test() {
        env
    }

    @Configuration
    @EnableConfigurationProperties(LawsConfigurationProperties::class)
    @Import(*[BeanReplicatorPostProcessor::class, TestVerifier::class])
    class Config {

    }
}