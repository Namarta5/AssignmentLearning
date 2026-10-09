package com.example.assignmentlearning.domain.model

import kotlin.math.roundToInt

object ProgressCalculator {
    fun percent(completed: Int, total: Int): Int {
        if (total <= 0) return 0
        return (completed * 100.0 / total).roundToInt().coerceIn(0, 100)
    }
}
