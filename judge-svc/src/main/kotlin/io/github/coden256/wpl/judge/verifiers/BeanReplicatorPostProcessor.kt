package io.github.coden256.wpl.judge.verifiers

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class BeanReplicatorPostProcessor : BeanPostProcessor, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext
    private val beanFactory get() =  applicationContext.autowireCapableBeanFactory as DefaultListableBeanFactory


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is Verifier<*>) {
            replicateBean(bean, beanName)
        }
        return bean
    }


    private fun replicateBean(originalBean: Verifier<*>, originalBeanName: String) {
        val config = originalBean::class.typeParameters.firstOrNull()

        // Define how many copies and what configurations you want
        val configurations = listOf(
            mapOf("property" to "value1", "priority" to 1),
            mapOf("property" to "value2", "priority" to 2),
            mapOf("property" to "value3", "priority" to 3)
        )

        configurations.forEachIndexed { index, config ->
            val newBeanName = "${originalBeanName}_copy_$index"
            
            // Create a new bean definition based on the original
            val beanDefinition = GenericBeanDefinition().apply {
                val originalDefinition = beanFactory.getBeanDefinition(originalBeanName)
                setBeanClassName(originalDefinition.beanClassName)
                propertyValues.addPropertyValues(originalDefinition.propertyValues)
                
                // Apply custom configuration
                config.forEach { (key, value) ->
                    propertyValues.add(key, value)
                }
            }
            
            // Register the new bean
            beanFactory.registerBeanDefinition(newBeanName, beanDefinition)
        }
    }
}