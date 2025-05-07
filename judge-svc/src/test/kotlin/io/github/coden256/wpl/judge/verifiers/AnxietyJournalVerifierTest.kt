package io.github.coden256.wpl.judge.verifiers

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest()
@Import(value = [AnxietyJournalVerifier::class])
class AnxietyJournalVerifierTest {

    @Autowired
    lateinit var anxietyJournalVerifier: AnxietyJournalVerifier

    @Test
    fun verify() {
        anxietyJournalVerifier.verify()
    }
}