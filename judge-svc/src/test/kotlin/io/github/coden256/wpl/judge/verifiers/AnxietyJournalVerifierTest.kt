package io.github.coden256.wpl.judge.verifiers

import org.apache.commons.lang3.Range
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

@SpringBootTest()
@Import(value = [AnxietyJournalVerifier::class])
@Disabled
class AnxietyJournalVerifierTest {

    @Autowired
    lateinit var anxietyJournalVerifier: AnxietyJournalVerifier

    @Test
    fun verify() {
        anxietyJournalVerifier.config = AnxietyJournalVerifier.Config(
            minWords = 100,
            cosineThreshold = 0.7,
            jaccardThreshold = 0.85,
            expiry = Duration.ofDays(5),
            cosineJaccardSumThreshold = 1.5,
        )
        anxietyJournalVerifier.verify()
    }
}