package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.wpl.judge.core.LawRuling
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.Environment
import reactor.core.publisher.Mono
import java.time.Instant


class JudgeLaw(
    private val verifiers: List<Verifier>,
    private val name: String,
    private val description: String?,
    private val rulings: List<LawRuling>,
    private val priority: Int
){

}


private const val PREFIX = "test"
@ConfigurationProperties(PREFIX)
data class LawsConfigurationProperties(
    private val laws: List<LawsConfigurationPropertiesEntry>
){
    fun toJudgeLaws(environment: Environment): List<JudgeLaw> {
        return laws.mapIndexed{index, entry ->
            JudgeLaw(
                getVerifiers(environment, entry.verify ?: listOf(), index),
                entry.name,
                entry.description,
                listOf(),
                index
            )
        }
    }

    fun getVerifiers(environment: Environment, verifiers: List<ReducedVerifierInfo>, index: Int): List<Verifier>{
        val path = PREFIX + ::laws.name + "[$index].verify"
        return verifiers.mapIndexed { vIndex, info -> getVerifier(path, environment, info, vIndex) }
    }

    fun getVerifier(prefix: String, environment: Environment, info: ReducedVerifierInfo, index: Int): Verifier {
        val path = "$prefix[$index]"
        val type = info.type
        val typeCfg = info.type + ".Config"
        return bindProperties()
    }

    fun <T> bindProperties(environment: Environment, prefix: String, targetClass: Class<T>): T {
        return Binder.get(environment)
            .bind(prefix, targetClass)
            .orElseThrow {
                IllegalStateException(
                    "Could not bind properties under prefix '" + prefix + "' to " + targetClass.name
                )
            }
    }

}



data class LawsConfigurationPropertiesEntry(
    val description: String,
    val name: String,
    val enabled: Boolean = true,
    val verify: List<ReducedVerifierInfo>? = null
)

data class ReducedVerifierInfo(val type: String)

interface Verifier  {
    fun verify(): Mono<Success>
}

data class Success(
    val reason: String,
    val expiry: Instant
)