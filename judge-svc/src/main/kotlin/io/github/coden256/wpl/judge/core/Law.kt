package io.github.coden256.wpl.judge.core

import io.github.coden256.wpl.judge.config.Operator
import io.github.coden256.wpl.judge.config.RulingSet
import io.github.coden256.wpl.judge.verifiers.AlwaysValidVerifier
import org.apache.logging.log4j.kotlin.Logging
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant

data class Law(
    val name: String,
    val operator: Operator,
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

        val result: Mono<Success> = when (operator){
            Operator.ANY -> Mono.firstWithValue(verificationResults)
            Operator.ALL -> Mono.zip(verificationResults) {
                it.asSequence().filterIsInstance<Success>().reduce { acc, success ->  acc.and(success)}
            }
            Operator.NONE -> Mono.firstWithValue(verificationResults)
                .switchIfEmpty{ Mono.error(IllegalArgumentException("Is expected. To be resumed later."))}
                .flatMap { Mono.empty<Success>() }
                .onErrorResume { Mono.just(Success("None of the conditions met: ${verifiers.map { it::class.simpleName }}", Instant.MAX)) }
        }

        return result
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

    private fun Success.and(other: Success): Success{
        return Success(this.reason + " AND " + other.reason, minOf(this.expiry, other.expiry))
    }

    private fun RulingSet.toRulings(reason: String, expiry: Instant): Map<String, Ruling>{
        return block.associateWith { Ruling(Action.BLOCK, reason, priority, expiry) } +
                force.associateWith { Ruling(Action.FORCE, reason, priority, expiry) } +
                allow.associateWith { Ruling(Action.ALLOW, reason, priority, expiry) }
    }
}
