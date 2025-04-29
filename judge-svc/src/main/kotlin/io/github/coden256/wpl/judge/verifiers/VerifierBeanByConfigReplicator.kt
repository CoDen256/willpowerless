package io.github.coden256.wpl.judge.verifiers

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

@Component
class VerifierBeanByConfigReplicator : BeanPostProcessor, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext
    private lateinit var beanFactory: DefaultListableBeanFactory
    private lateinit var verifierDefinitionProvider: VerifierDefinitionProvider

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
        this.beanFactory = applicationContext.autowireCapableBeanFactory as DefaultListableBeanFactory
        this.verifierDefinitionProvider = applicationContext.getBean(VerifierDefinitionProvider::class.java)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is Verifier<*>) {
            replicateBeanWithConfig(bean, beanName)
            return null
        }
        return bean
    }

    private fun replicateBeanWithConfig(
        originalBean: Verifier<*>,
        originalBeanName: String,
    ) {
        val originalDefinition = beanFactory.getBeanDefinition(originalBeanName)
        val configClass = originalBean::class
            .supertypes
            .flatMap { it.arguments }
            .firstOrNull { it.type?.isSubtypeOf(VerifierConfig::class.createType()) == true }
            ?.type
            ?.classifier as? KClass<*>
            ?: return


        verifierDefinitionProvider
            .getVerifierDefinitionsByClass(originalBean::class)
            .forEachIndexed { index, definition ->
                val newBeanName = "${originalBeanName}_${definition.parent}_${definition.index}"

                // Create a new bean definition based on the original
                val beanDefinition = GenericBeanDefinition().apply {
                    beanClassName = originalDefinition.beanClassName
                    propertyValues.addPropertyValues(originalDefinition.propertyValues)

                    val config = bindProperties(applicationContext.environment, definition.path, configClass.java)
                    propertyValues.add(Verifier<*>::config.name, config)
                }

                beanFactory.registerBeanDefinition(newBeanName, beanDefinition)
            }
        beanFactory.removeBeanDefinition(originalBeanName)
    }

    fun <T> bindProperties(environment: Environment, prefix: String, targetClass: Class<T>): T {
        applicationContext.environment
        return Binder.get(environment)
            .bind(prefix, targetClass)
            .orElseThrow { IllegalStateException("Could not bind properties under prefix '" + prefix + "' to " + targetClass.name) }
    }
}