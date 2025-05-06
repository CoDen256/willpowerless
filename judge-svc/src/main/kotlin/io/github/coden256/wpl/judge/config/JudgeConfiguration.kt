package io.github.coden256.wpl.judge.config

import io.github.coden256.wellpass.config.WellpassConfiguration
import io.github.coden256.wpl.judge.components.LawAggregatingJudge
import io.github.coden256.wpl.judge.components.LocalTimeRangeConverter
import io.github.coden256.wpl.judge.components.VerifierBeanByConfigReplicator
import io.github.coden256.wpl.judge.config.RulingSet.Companion.merge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.verifiers.CalenderAbsenceVerifier
import io.github.coden256.wpl.judge.verifiers.ScheduleVerifier
import io.github.coden256.wpl.judge.verifiers.WellpassVerifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

@Configuration
@EnableConfigurationProperties(MultipleLawProperties::class)
class JudgeConfiguration {

    @Bean
    fun replicator(): VerifierBeanByConfigReplicator{
        return VerifierBeanByConfigReplicator()
    }

    @Bean
    fun laws(properties: MultipleLawProperties, verifiers: List<Verifier<*>>, environment: Environment): List<Law> {
        val verifiersByParent = verifiers.groupBy { it.definition.parent }

        return properties.def.mapIndexed { index, lawDefinition ->
            Law(
                lawDefinition.name,
                verifiersByParent[lawDefinition.name] ?: emptyList(),
                lawDefinition.out.merge(),
                lawDefinition.description,
                index,
                lawDefinition.enabled
            )
        }
    }
}

