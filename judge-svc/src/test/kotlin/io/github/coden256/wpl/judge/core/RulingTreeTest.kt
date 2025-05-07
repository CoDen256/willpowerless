package io.github.coden256.wpl.judge.core

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.coden256.wpl.judge.core.Action.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class RulingTreeTest {

    private val mapper = ObjectMapper()

    @Test
    fun getRuling() {
        val root = RulingTree()

        root.add("/dev", FORCE)

        assertEquals(
            mapper.readTree("""{"dev": {"ruling": {"action": "FORCE"}}}"""),
            root.json()
        )

        root.add("/dev/0", BLOCK)
        assertEquals(
            mapper.readTree("""{"dev": {"ruling": {"action":"FORCE"}, "0": {"ruling": {"action":"BLOCK"}}}}"""),
            root.json()
        )

        assertEquals(
            mapper.readTree("""{"ruling": {"action":"FORCE"}, "0": {"ruling": {"action":"BLOCK"}}}"""),
            root.get("/dev")
        )

        assertEquals(
            mapper.readTree("""{"action":"FORCE"}"""),
            root.get("/dev/ruling")
        )

        assertEquals(
            mapper.readTree("""{"ruling": {"action":"BLOCK"}}"""),
            root.get("/dev/0")
        )

        root.add("/dev/mi/apps/*", BLOCK)
        root.add("/dev/mi/apps/telegram.beta/channels/*", Ruling(BLOCK, "no telegram channels"))
        root.add("/dev/mi/apps/telegram.beta/channels/something", Ruling(FORCE, "allow"))
        root.add("/dev/mi/apps/telegram.beta/channels/telegra*", Ruling(ALLOW, "allow telegram"))
        root.add("/dev/mi/apps/telegram.beta", FORCE)

        assertEquals(
            mapper.readTree("""
                {"dev": {
                "ruling": {"action":"FORCE"}, 
                "0": {"ruling": {"action":"BLOCK"}},
                "mi": {
                    "apps": {
                        "telegram.beta": {
                            "ruling": {"action":"FORCE"},
                            "channels": {
                            
                                "telegra*": {"ruling": {"action":"ALLOW", "reason": "allow telegram"}},
                                "something": {"ruling": {"action":"FORCE", "reason": "allow"}},
                                "*": {
                                    "ruling": {"action":"BLOCK", "reason": "no telegram channels"}
                              
                                }
                            }
                        },
                        "*": {
                            "ruling": {"action":"BLOCK"}
                        }
                        
                    }
                }
                }
                }
                """.trimIndent()),
            root.json()
        )


        assertEquals(
            mapper.readTree("""null"""),
            root.get("/dev/mi/apps/telegram.beta/channels/*/rulings")
        )

        assertEquals(
            mapper.readTree("""{"action":"BLOCK", "reason": "no telegram channels"}"""),
            root.get("/dev/mi/apps/telegram.beta/channels/*/ruling")
        )

        assertEquals(
            mapper.readTree("""{"action":"BLOCK", "reason": "no telegram channels"}"""),
            root.get("/dev/mi/apps/telegram.beta/channels/some/ruling")
        )

        assertEquals(
            mapper.readTree("""{"action":"ALLOW", "reason": "allow telegram"}"""),
            root.get("/dev/mi/apps/telegram.beta/channels/telegram/ruling")
        )

        assertEquals(
            mapper.readTree("""{"action":"FORCE", "reason": "allow"}"""),
            root.get("/dev/mi/apps/telegram.beta/channels/something/ruling")
        )


    }

    @Test
    fun merge(){
        val root = RulingTree()

        root.add("/a", FORCE)
        root.add("/a", BLOCK)

        root.add("/b", BLOCK)
        root.add("/b", FORCE)

        root.add("/c", BLOCK)
        root.add("/c", ALLOW)

        root.add("/d", FORCE)
        root.add("/d", ALLOW)

        assertEquals(
            mapper.readTree("""{
                "a": {"ruling":{"action":"FORCE"}},
                "b": {"ruling":{"action":"FORCE"}},
                "c": {"ruling":{"action":"BLOCK"}},
                "d": {"ruling":{"action":"FORCE"}}
                }""".trimIndent()),
            root.get("/")
        )
    }

}