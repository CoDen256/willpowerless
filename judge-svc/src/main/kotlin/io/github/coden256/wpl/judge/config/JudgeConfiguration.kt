package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.config.RulingSet.Companion.merge
import io.github.coden256.wpl.judge.core.Law
import io.github.coden256.wpl.judge.core.Verifier
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.getBeansOfType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MultipleLawProperties::class)
class JudgeConfiguration {

    @Bean
    fun laws(properties: MultipleLawProperties, beanFactory: ListableBeanFactory): List<Law> {
        val old = beanFactory.getBeansOfType<Verifier<*>>().values // uuhhh, second time is ok?
        val verifiers = beanFactory.getBeansOfType<Verifier<*>>().values - old
        val verifiersByParent = verifiers.groupBy { it.definition.parent }

        return properties.def.mapIndexed { index, lawDefinition ->
            Law(
                lawDefinition.name,
                lawDefinition.operator,
                verifiersByParent[lawDefinition.name] ?: emptyList(),
                lawDefinition.out.merge(),
                lawDefinition.description,
                index,
                lawDefinition.enabled
            )
        }
    }
}

