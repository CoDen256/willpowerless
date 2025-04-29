package io.github.coden256.wpl.judge.verifiers

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.reflect.KClass

interface Verifier<C : VerifierConfig> {
    var config: C
    fun verify(): Mono<Success>
}

abstract class VerifierConfig{
    var definition: VerifierDefinition? = null
}

interface VerifierDefinitionProvider {
    fun getVerifierDefinitions(): List<VerifierDefinition>

    fun getVerifierDefinitionsByType(): Map<String, List<VerifierDefinition>>{
        return getVerifierDefinitions().groupBy { it.type }
    }

    fun getVerifierDefinitionsByType(name: String): List<VerifierDefinition>{
        return getVerifierDefinitionsByType()[name] ?: emptyList()
    }
    fun getVerifierDefinitionsByClass(`class`: KClass<*>): List<VerifierDefinition> {
        return `class`.simpleName?.let { getVerifierDefinitionsByType(it) } ?: emptyList()
    }
}

data class Success(
    val reason: String,
    val expiry: Instant
)

data class VerifierDefinition(
    val type: String,
    val path: String,
    val parent: String,
    val index: Int
)