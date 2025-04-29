package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.core.LawRuling
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.reflect.KClass


class JudgeLaw(
    private val verifiers: List<Verifier<*>>,
    private val name: String,
    private val description: String?,
    private val rulings: List<LawRuling>,
    private val priority: Int
)


interface VerifierDefinitionProvider {
    fun getVerifierDefinitions(): Map<String, List<VerifierDefinition>>
    fun getVerifierDefinitionsByClass(`class`: KClass<*>): List<VerifierDefinition>
}

private const val PREFIX = "judge"

@ConfigurationProperties(PREFIX)
data class MultipleLawsProperties(
    private val laws: List<LawProperties>
) : VerifierDefinitionProvider {

    private val definitions = loadDefinitions()

    private fun loadDefinitions(): Map<String, List<VerifierDefinition>> {
        return laws.flatMapIndexed { index, law -> getVerifierDefinitions(law, index) }
            .groupBy { it.type }
    }

    private fun getVerifierDefinitions(
        parent: LawProperties,
        parentIndex: Int
    ): List<VerifierDefinition> {
        val definitions = parent.verify ?: return emptyList()
        val definitionPath: (Int) -> String = {
            "$PREFIX.${::laws.name}[$parentIndex].${LawProperties::verify.name}[$it]"
        }

        return definitions.mapIndexed { index, it ->
            VerifierDefinition(
                it.type,
                definitionPath(index),
                parent.name,
                index
            )
        }
    }

    override fun getVerifierDefinitions(): Map<String, List<VerifierDefinition>> {
        return definitions
    }

    override fun getVerifierDefinitionsByClass(`class`: KClass<*>): List<VerifierDefinition> {
        return definitions[`class`.simpleName] ?: emptyList()
    }
}

data class LawProperties(
    val description: String,
    val name: String,
    val enabled: Boolean = true,
    val verify: List<ReducedVerifierDefinition>? = null
) {
    data class ReducedVerifierDefinition(val type: String)
}

data class VerifierDefinition(
    val type: String,
    val path: String,
    val parent: String,
    val index: Int
)

interface Verifier<C : VerifierConfig> {
    var config: C
    var definition: VerifierDefinition?
    fun verify(): Mono<Success>
}

interface VerifierConfig


@Component
class TestVerifier(
    val env: Environment
) : Verifier<TestVerifier.Config> {

    override var config: Config = Config()

    override fun verify(): Mono<Success> {
        TODO("Not yet implemented")
    }

    data class Config(
        val type: String? = null
    ) : VerifierConfig
}

data class Success(
    val reason: String,
    val expiry: Instant
)