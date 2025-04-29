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
class BeanReplicatorPostProcessor : BeanPostProcessor, ApplicationContextAware {

    companion object {
        val CONFIG_TYPE = VerifierConfig::class.createType()
    }


    private lateinit var applicationContext: ApplicationContext
    private val beanFactory get() =  applicationContext.autowireCapableBeanFactory as DefaultListableBeanFactory

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is Verifier<*>) {
            replicateBeanWithConfig(bean, beanName, listOf(
                "test.laws[0].verify[0]",
                "test.laws[1].verify[0]",
            ))
            return null
        }
        return bean
    }


    private fun replicateBeanWithConfig(
        originalBean: Verifier<*>,
        originalBeanName: String,
        configurationsPath: List<String>
    ) {
        val originalDefinition = beanFactory.getBeanDefinition(originalBeanName)
        val configClass = originalBean::class
            .supertypes
            .flatMap{it.arguments }
            .firstOrNull { it.type?.isSubtypeOf(CONFIG_TYPE) == true }
            ?.type
            ?.classifier as? KClass<*>
            ?: return


        configurationsPath.forEachIndexed { index, path ->
            val newBeanName = "${originalBeanName}_copy_$index"
            
            // Create a new bean definition based on the original
            val beanDefinition = GenericBeanDefinition().apply {
                beanClassName = originalDefinition.beanClassName
                propertyValues.addPropertyValues(originalDefinition.propertyValues)

                val config = bindProperties(applicationContext.environment, path, configClass.java)
                propertyValues.add(Verifier<*>::config.name, config)
            }

            
            // Register the new bean
            beanFactory.registerBeanDefinition(newBeanName, beanDefinition)
        }
        beanFactory.removeBeanDefinition(originalBeanName)
    }

    fun <T> bindProperties(environment: Environment, prefix: String, targetClass: Class<T>): T {
        applicationContext.environment
        return Binder.get(environment)
            .bind(prefix, targetClass)
            .orElseThrow {
                IllegalStateException(
                    "Could not bind properties under prefix '" + prefix + "' to " + targetClass.name
                )
            }
    }
}