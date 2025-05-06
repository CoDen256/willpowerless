package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.verifiers.VerifierDefinition
import io.github.coden256.wpl.judge.verifiers.VerifierDefinitionProvider
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "judge"
@ConfigurationProperties(PREFIX)
data class MultipleLawProperties(
    val laws: List<LawProperties>
) : VerifierDefinitionProvider {

    private val definitions = loadDefinitions()

    private fun loadDefinitions(): List<VerifierDefinition> {
        return laws.flatMapIndexed { index, law -> getVerifierDefinitions(law, index) }
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

    override fun getVerifierDefinitions(): List<VerifierDefinition> {
        return definitions
    }
}

data class LawProperties(
    val description: String,
    val name: String,
    val enabled: Boolean = true,
    val verify: List<ReducedVerifierDefinition>? = null,
    val out: List<String>
) {
    data class ReducedVerifierDefinition(val type: String)
}