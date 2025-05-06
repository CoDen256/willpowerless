package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.config.RulingSet.Companion.merge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MultipleLawProperties::class, MultipleRulingProperties::class)
class JudgeConfiguration {

    @Bean
    fun laws(properties: MultipleLawProperties, verifiers: List<Verifier<*>>): List<Law> {
        val verifiersByParent = verifiers.groupBy { it.definition.parent }

        return properties.laws.mapIndexed { index, lawDefinition ->
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

