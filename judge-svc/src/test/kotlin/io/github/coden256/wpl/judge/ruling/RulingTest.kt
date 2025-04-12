package io.github.coden256.wpl.judge.ruling

import org.junit.jupiter.api.Assertions.*
import  io.github.coden256.wpl.judge.ruling.Action.*

import org.junit.jupiter.api.Test

class RulingTest {

    @Test
    fun getRuling() {

        assertEquals("/stuff/echo", Ruling(BLOCK, "/stuff/echo").path)
        assertEquals("/type/id", Ruling(BLOCK, "/type/id").path)
        assertEquals("/type/id/type/id", Ruling(BLOCK, "/type/id/type/id").path)
        assertThrows(InvalidPathRootException::class.java) { Ruling(BLOCK, "stuff") }
        assertThrows(InvalidPathException::class.java) { Ruling(BLOCK, "/stuff") }
        assertThrows(InvalidPathException::class.java) { Ruling(BLOCK, "/") }
        assertThrows(InvalidPathException::class.java) { Ruling(BLOCK, "/stuff/s/s") }


        assertThrows(InvalidPathException::class.java) {
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks") }

        assertThrows(InvalidSubRulingException::class.java) {
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo") }



        assertEquals(Ruling(BLOCK, "/devices/*/networks/*"),
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/*/networks/*")
        )

        assertEquals(Ruling(BLOCK, "/devices/echo/networks/*"),
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks/*")
        )

        assertEquals(Ruling(BLOCK, "/devices/echo/networks/wifi"),
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks/wifi")
        )

        assertEquals(Ruling(BLOCK, "/devices/*/networks/wifi"),
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/*/networks/wifi")
        )

        assertEquals(Ruling(BLOCK, "/devices/echo/networks/wifi/something/else"),
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/echo/networks/wifi/something/else")
        )

        assertEquals(Ruling(BLOCK, "/devices/*/networks/*/something/else"),
            Ruling(BLOCK, "/devices/*/networks/*").getSubRuling("/devices/*/networks/*/something/else")
        )


//        Ruling(Action.BLOCK, Path("/stuff"))

    }

    @Test
    fun getPath() {

    }
}