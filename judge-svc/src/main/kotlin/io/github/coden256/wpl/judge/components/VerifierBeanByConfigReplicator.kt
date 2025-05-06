package io.github.coden256.wpl.judge.components

import io.github.coden256.wpl.judge.core.Verifier
import io.github.coden256.wpl.judge.core.VerifierConfig
import io.github.coden256.wpl.judge.core.VerifierDefinitionProvider
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.Ordered
import org.springframework.core.PriorityOrdered
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

@Component
class VerifierBeanByConfigReplicator : BeanPostProcessor, ApplicationContextAware
//    PriorityOrdered
{

    private lateinit var applicationContext: ApplicationContext
    private lateinit var beanFactory: DefaultListableBeanFactory
    private lateinit var verifierDefinitionProvider: VerifierDefinitionProvider

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
        this.beanFactory = applicationContext.autowireCapableBeanFactory as DefaultListableBeanFactory
        this.verifierDefinitionProvider = applicationContext.getBean(VerifierDefinitionProvider::class.java)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is Verifier<*> && !beanName.contains("copy")) {
            replicateBeanWithConfig(bean, beanName)
            return null
        }
        return bean
    }

    @Suppress("UNCHECKED_CAST")
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
            ?.classifier as? KClass<out VerifierConfig>
            ?: return

        verifierDefinitionProvider
            .getVerifierDefinitionsByClass(originalBean::class)
            .forEachIndexed { index, definition ->
                val newBeanName = "${originalBeanName}_copy_${definition.parentIndex}_${definition.index}"

                // Create a new bean definition based on the original
                val beanDefinition = GenericBeanDefinition().apply {
                    beanClassName = originalDefinition.beanClassName
                    propertyValues.addPropertyValues(originalDefinition.propertyValues)

                    val config: VerifierConfig = bindProperties(definition.path, configClass.java)
                    propertyValues.add(Verifier<*>::config.name, config)
                    propertyValues.add(Verifier<*>::definition.name, definition)
                }

                beanFactory.registerBeanDefinition(newBeanName, beanDefinition)
            }
        beanFactory.removeBeanDefinition(originalBeanName)
    }

    fun <T> bindProperties(prefix: String, targetClass: Class<T>): T {
        return applicationContext
            .getBinder()
            .bind(prefix, targetClass)
            .orElseThrow { IllegalStateException("Could not bind properties under prefix '" + prefix + "' to " + targetClass.name) }
    }

    fun ApplicationContext.getBinder(): Binder {
        try {
            val binder = beanFactory.getBean("org.springframework.boot.context.internalConfigurationPropertiesBinder")
            val getBinderMethod = binder::class.java.getDeclaredMethod("getBinder")
            getBinderMethod.isAccessible = true
            return getBinderMethod.invoke(binder) as Binder
        }catch (e: Exception){
            e.printStackTrace()
            return Binder.get(applicationContext.environment)
        }
    }

//    override fun getOrder(): Int {
//        return Ordered.HIGHEST_PRECEDENCE;
//    }
}