package io.github.coden256.wpl.judge.verifiers

import io.github.coden256.calendar.ICSCalendar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled

import org.junit.jupiter.api.Test

@Disabled
 class CalenderAbsenceVerifierTest {

@Test
 fun verify() {
 val calenderAbsenceVerifier =
  CalenderAbsenceVerifier(ICSCalendar(""))

 calenderAbsenceVerifier.verify().block()
}
}