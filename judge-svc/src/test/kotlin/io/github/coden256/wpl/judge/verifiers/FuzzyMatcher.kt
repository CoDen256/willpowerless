package io.github.coden256.wpl.judge.verifiers

import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.similarity.CosineSimilarity
import org.apache.commons.text.similarity.JaccardSimilarity
import org.apache.commons.text.similarity.LevenshteinDistance
import org.junit.jupiter.api.Test

class FuzzyMatcher {
    @Test
    fun name() {
        PlagiarismDetector(listOf("hello", "goodbay"))
        val stuff = LevenshteinDistance.getDefaultInstance().apply("hello", "hel")
    }
}

