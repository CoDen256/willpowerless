package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.config.MultipleLawProperties
import io.github.coden256.wpl.judge.config.RulingRegistry
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.verifiers.Verifier
import org.springframework.stereotype.Component

class JudgeLaw(
    private val verifiers: List<Verifier<*>>,
    private val name: String,
    private val description: String?,
    private val rulings: List<LawRuling>,
    private val priority: Int
){

}

@Component
class JudgeLawProvider(
    private val properties: MultipleLawProperties,
    private val verifiers: List<Verifier<*>>,
    private val registry: RulingRegistry
){

    fun get(): List<JudgeLaw> {
        val verifiersByParent = verifiers.groupBy { it.config.definition?.parent }
        return properties.laws.mapIndexed { index, law ->
            JudgeLaw(
                verifiersByParent.get(law.name) ?: emptyList(),
                law.name,
                law.description,
                registry.getRules(law.out),
                index
            )
        }
    }
}