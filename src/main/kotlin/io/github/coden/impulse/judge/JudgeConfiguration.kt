package io.github.coden.impulse.judge

import io.github.coden.wellpass.api.config.WellpassConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration()
@Import(WellpassConfiguration::class)
class JudgeConfiguration {
}