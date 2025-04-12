package io.github.coden256.wpl.judge.ruling

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.coden256.utils.read
import org.junit.jupiter.api.Assertions.*
import  io.github.coden256.wpl.judge.ruling.Action.*

import org.junit.jupiter.api.Test

class RulingTest {

    private val mapper = ObjectMapper()

    @Test
    fun getRuling() {
        val root = RulingNode()

        root.add("/dev", FORCE)

        assertEquals(
            mapper.readTree("""{"dev": {"ruling": "FORCE"}}"""),
            root.json()
        )

        root.add("/dev/0", BLOCK)

        assertEquals(
            mapper.readTree("""{"dev": {"ruling": "FORCE", "0": {"ruling": "BLOCK"}}}"""),
            root.json()
        )

//        assertEquals("/stuff/echo", Ruling(BLOCK, "/stuff/echo").path)
//        assertEquals("/type/id", Ruling(BLOCK, "/type/id").path)
//        assertEquals("/type/id/type/id", Ruling(BLOCK, "/type/id/type/id").path)
//        assertThrows(InvalidPathRootException::class.java) { Ruling(BLOCK, "stuff") }
//        assertThrows(InvalidPathException::class.java) { Ruling(BLOCK, "/stuff") }
//        assertThrows(InvalidPathException::class.java) { Ruling(BLOCK, "/") }
//        assertThrows(InvalidPathException::class.java) { Ruling(BLOCK, "/stuff/s/s") }
//
//
//        assertThrows(InvalidPathException::class.java) {
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks") }
//
//        assertThrows(InvalidSubRulingException::class.java) {
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo") }
//
//
//
//        assertEquals(Ruling(BLOCK, "/devices/*/networks/*"),
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/*/networks/*")
//        )
//
//        assertEquals(Ruling(BLOCK, "/devices/echo/networks/*"),
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks/*")
//        )
//
//        assertEquals(Ruling(BLOCK, "/devices/echo/networks/wifi"),
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks/wifi")
//        )
//
//        assertEquals(Ruling(BLOCK, "/devices/*/networks/wifi"),
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/*/networks/wifi")
//        )
//
//        assertEquals(Ruling(BLOCK, "/devices/echo/networks/wifi/something/else"),
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks/wifi/something/else")
//        )
//
//        assertEquals(Ruling(BLOCK, "/devices/*/networks/*/something/else"),
//            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/*/networks/*/something/else")
//        )


//        Ruling(Action.BLOCK, Path("/stuff"))

    }

    @Test
    fun getPath() {

//        assertEquals("/", Path("/").head())
//        assertEquals("/", Path("/").head())

    }
}