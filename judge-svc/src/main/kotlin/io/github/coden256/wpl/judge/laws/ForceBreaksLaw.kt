package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.config.RulingRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = ["laws.${ForceBreaksLaw.NAME}.enabled"], matchIfMissing = true)
class ForceBreaksLaw(
    env: Environment,
    registry: RulingRegistry
) : StaticLaw(NAME, env, registry){
    companion object { const val NAME = "force-breaks" }
}