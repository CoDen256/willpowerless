package io.github.coden256.wpl.judge.ruling

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.coden256.wpl.judge.ruling.Action.BLOCK
import io.github.coden256.wpl.judge.ruling.Action.FORCE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RulingTest {

    private val mapper = ObjectMapper()

    @Test
    fun getRuling() {
        val root = RulingNode()

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


//        assertEquals(
//            mapper.readTree("""{"dev": {"action": "FORCE"}}"""),
//            root.get("/dev/mi/apps/telegram.beta/channels/*")
//        )

    }

}