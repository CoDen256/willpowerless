package io.github.coden256.wpl.judge.firewall

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/firewall")
class JudgeFirewallRuleController() {

    @GetMapping("/")
    fun index(): String {
        return "Hi."
    }

    @GetMapping("/rules")
    fun rules(): FirewallRules {
        return FirewallRules(
            FirewallRule("rule0", listOf("a","b"), null, "0"),
            FirewallRule("rule1", null, "c", "1")
        )
    }

    class FirewallRules(rules: List<FirewallRule>): Map<String, FirewallRule> by HashMap(
        rules.withIndex().associate { it.index.toString()  to it.value}
    ){
        constructor(vararg rules: FirewallRule): this(rules.toList())
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class FirewallRule(
        val name: String,
        @JsonProperty("src_mac")
        val srcMac: List<String>?,
        val dest: String?,
        val enabled: String
    )
}