package io.github.coden256.wpl.judge.laws

import io.github.coden256.wpl.judge.config.RulingRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = ["laws.${ModeratePLaw.NAME}.enabled"], matchIfMissing = true)
class ModeratePLaw(
    env: Environment,
    registry: RulingRegistry
) : StaticLaw(NAME, env, registry){
    companion object { const val NAME = "moderate-p" }
}

@Component
@ConditionalOnProperty(value = ["laws.${ModerateSocialLaw.NAME}.enabled"], matchIfMissing = true)
class ModerateSocialLaw(
    env: Environment,
    registry: RulingRegistry
) : StaticLaw(NAME, env, registry){
    companion object { const val NAME = "moderate-social" }
}

@Component
@ConditionalOnProperty(value = ["laws.${AllowBeamerLaw.NAME}.enabled"], matchIfMissing = true)
class AllowBeamerLaw(
    env: Environment,
    registry: RulingRegistry
) : StaticLaw(NAME, env, registry){
    companion object { const val NAME = "allow-beamer" }
}

