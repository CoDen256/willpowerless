package io.github.coden256.wpl.judge.core

import io.github.coden256.wpl.judge.config.RulingSet
import io.github.coden256.wpl.judge.verifiers.AlwaysValidVerifier
import org.apache.logging.log4j.kotlin.Logging
import reactor.core.publisher.Mono
import java.time.Instant

data class Law(
    val name: String,
    val verifiers: List<Verifier<*>>,
    val rulingSet: RulingSet,
    val description: String,
    val priority: Int,
    val enabled: Boolean
): Logging{

    val effectiveVerifers
        get() = verifiers.ifEmpty { listOf(AlwaysValidVerifier) }

    fun verify(): Mono<RulingTree> {
        val verificationResults: List<Mono<Success>> = effectiveVerifers.map { it.verify() }

        return Mono
            .firstWithValue(verificationResults)
            .onErrorComplete()
            .switchIfEmpty(Mono.empty())
            .map { success ->
                val root = RulingTree()
                val rulings = rulingSet.toRulings(name + ": " +success.reason, success.expiry)

                rulings.forEach { (path, rule) ->
                    logger.info { "[$name]: $path -> $rule" }
                    root.add(path, rule)
                }

                root
            }
    }

    private fun RulingSet.toRulings(reason: String, expiry: Instant): Map<String, Ruling>{
        return block.associateWith { Ruling(Action.BLOCK, reason, priority, expiry) } +
                force.associateWith { Ruling(Action.FORCE, reason, priority, expiry) } +
                allow.associateWith { Ruling(Action.ALLOW, reason, priority, expiry) }
    }
}
