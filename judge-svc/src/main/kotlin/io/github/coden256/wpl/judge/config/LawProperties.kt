package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.verifiers.VerifierDefinition
import io.github.coden256.wpl.judge.verifiers.VerifierDefinitionProvider
import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "laws"
@ConfigurationProperties(PREFIX)
class MultipleLawProperties : ArrayList<LawProperties>(), VerifierDefinitionProvider {

    private val definitions = loadDefinitions()

    private fun loadDefinitions(): List<VerifierDefinition> {
        return flatMapIndexed { index, law -> getVerifierDefinitions(law, index) }
    }

    private fun getVerifierDefinitions(
        parent: LawProperties,
        parentIndex: Int
    ): List<VerifierDefinition> {
        val definitions = parent.verify ?: return emptyList()
        val definitionPath: (Int) -> String = {
            "$PREFIX[$parentIndex].${LawProperties::verify.name}[$it]"
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
    val out: List<Rule>
) {
    data class ReducedVerifierDefinition(val type: String)
}

data class Rule(
    val block: List<String>,
    val force: List<String>
)