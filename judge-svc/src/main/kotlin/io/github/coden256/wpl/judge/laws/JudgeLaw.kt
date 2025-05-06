package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.config.MultipleLawProperties
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.verifiers.Verifier
import org.springframework.stereotype.Component

data class JudgeLaw(
    private val name: String,
    private val verifiers: List<Verifier<*>>,
    private val rulings: List<LawRuling>,
    private val description: String?,
    private val priority: Int
){

}

@Component
class JudgeLawProvider(
    private val properties: MultipleLawProperties,
    private val verifiers: List<Verifier<*>>,
){

    init {
        properties
    }

    fun get(): List<JudgeLaw> {
        val verifiersByParent = verifiers.groupBy { it.definition.parent }
        return properties.mapIndexed { index, law ->
            JudgeLaw(
                law.name,
                verifiersByParent[law.name] ?: emptyList(),
                emptyList(),
                law.description,
                index
            )
        }
    }
}