package io.github.coden256.wpl.judge

import io.github.coden256.wellpass.Wellpass
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.assertTrue

@SpringBootTest
class ImpulseRulingSetApplicationTests {

    @Autowired
    lateinit var api: Wellpass

    @Test
    fun contextLoads(){

    }

    @Test
    @Disabled
    fun wellpass() {
        val block = api.checkins(LocalDate.now()).block()
        assertTrue(!block?.checkIns.isNullOrEmpty())
    }

}
