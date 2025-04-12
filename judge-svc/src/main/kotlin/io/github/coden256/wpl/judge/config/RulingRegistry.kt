package io.github.coden256.wpl.judge.config

import io.github.coden256.wpl.judge.core.Action
import io.github.coden256.wpl.judge.core.LawRuling
import io.github.coden256.wpl.judge.core.Ruling
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component


interface RulingRegistry {
    fun getRules(categoryIds: List<String>): List<LawRuling>
}


@Component
@EnableConfigurationProperties(RulingProperties::class)
class ConfigurationPropertiesRulingRegistry(properties: RulingProperties) : RulingRegistry {
    private val categories = properties.asRulingCategories()
    override fun getRules(categoryIds: List<String>): List<LawRuling> {
        return categoryIds.map {
            categories.getOrDefault(it, emptyList())
        }.reduce { a, b -> a + b }
    }
}

@ConfigurationProperties(prefix = "")
data class RulingProperties(
    val rulings: Map<String, Map<String, List<String>>>
) {
    fun asRulingCategories(): Map<String, List<LawRuling>> {
        val result = mutableMapOf<String, List<LawRuling>>()

        for ((categoryId, rulings) in rulings) {
            val category = mutableListOf<LawRuling>()
            for ((action, paths) in rulings) {
                val actualAction = Action.valueOf(action.uppercase())
                for (path in paths) {
                    category.add(LawRuling(path, Ruling(actualAction, categoryId)))
                }
            }
            result[categoryId] = category
        }

        return result
    }
}
