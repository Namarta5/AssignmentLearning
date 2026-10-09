package com.example.assignmentlearning.domain

import com.example.assignmentlearning.domain.model.ProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {
    @Test fun percent_handlesZeroRoundingAndFull() {
        assertEquals(0, ProgressCalculator.percent(0, 0))
        assertEquals(33, ProgressCalculator.percent(1, 3))
        assertEquals(75, ProgressCalculator.percent(3, 4))
        assertEquals(100, ProgressCalculator.percent(4, 4))
    }
}
