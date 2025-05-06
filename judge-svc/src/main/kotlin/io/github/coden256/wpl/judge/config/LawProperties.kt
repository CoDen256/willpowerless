package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.core.VerifierDefinition
import io.github.coden256.wpl.judge.core.VerifierDefinitionProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


// TODO ROLE_INFRASTRUCTURE
@ConfigurationProperties(prefix = MultipleLawProperties.PREFIX)
data class MultipleLawProperties(
    val def: List<LawProperties>,
) : VerifierDefinitionProvider {
    companion object {
        const val PREFIX = "law"
    }

    private val definitions = loadDefinitions()

    private fun loadDefinitions(): List<VerifierDefinition> {
        return def.flatMapIndexed { index, law -> getVerifierDefinitions(law, index) }
    }

    private fun getVerifierDefinitions(
        parent: LawProperties,
        parentIndex: Int
    ): List<VerifierDefinition> {
        val definitions = parent.verify ?: return emptyList()
        val definitionPath: (Int) -> String = {
            "$PREFIX.${::def.name}[$parentIndex].${LawProperties::verify.name}[$it]"
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
    val out: List<RulingSet>
) {
    data class ReducedVerifierDefinition(val type: String)
}

//@ConfigurationProperties(MultipleRulingProperties.PREFIX)
class MultipleRulingProperties(val def: Map<String, RulingSet>) {
    companion object {
        const val PREFIX = "rule"
    }
}

data class RulingSet(
    val ref: String, // MUST INIT
    val block: List<String> = emptyList(),
    val force: List<String> = emptyList()
) {
    companion object {
        fun List<RulingSet>.merge(): RulingSet {
            return RulingSet(
                joinToString(",") { it.ref },
                flatMap { it.block },
                flatMap { it.force }
            )
        }
    }
}

@ConfigurationPropertiesBinding
@Component
class RuleConverter(val environment: Environment) : Converter<String, RulingSet> {
    override fun convert(source: String): RulingSet {
        try {
            return parseEntry(source)
        } catch (e: Exception) {
            throw Exception("Unable to parse rule: $source", e)
        }
    }

    private fun parseEntry(name: String): RulingSet {
        return Binder
            .get(environment)
            .bind(
                "${MultipleRulingProperties.PREFIX}.${MultipleRulingProperties::def.name}.$name",
                RulingSet::class.java
            )
            .get()
    }
}