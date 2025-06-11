package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.config.RulingSet.Companion.merge
import io.github.coden256.wpl.judge.core.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalTime
import kotlinx.datetime.toKotlinTimeZone
import org.apache.commons.lang3.Range
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.getBeansOfType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

@Configuration
@EnableConfigurationProperties(MultipleLawProperties::class, BudgetProperties::class)
class JudgeConfiguration {

    @Bean
    fun budgetStorage(properties: BudgetProperties): SessionStorage{
        return SessionFileStorage(properties.store)
    }

    @Bean
    fun oneShotBudget(sessionStorage: SessionStorage, budgetProvider: (LocalDateTime) -> Duration, properties: BudgetProperties): RequestAwareOneShotBudget {
        return RequestAwareOneShotBudget(sessionStorage, budgetProvider, properties.start.toKotlinLocalTime(), properties.total.toKotlinDuration(), properties.tz.toKotlinTimeZone())
    }

    @Bean
    fun budgetProvider(properties: BudgetProperties): (LocalDateTime) -> Duration{
        return MinMaxBudget(
            properties.start.toKotlinLocalTime(), properties.total.toKotlinDuration(),
            Range.of(properties.minDuration.toKotlinDuration(), properties.maxDuration.toKotlinDuration()),
            properties.curve
        )
    }

    @Bean
    fun laws(properties: MultipleLawProperties, beanFactory: ListableBeanFactory): List<Law> {
        val old = beanFactory.getBeansOfType<Verifier<*>>().values // uuhhh, second time is ok?
        val new = beanFactory.getBeansOfType<Verifier<*>>().values
        val verifiers = new - old
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

