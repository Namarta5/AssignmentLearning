package com.example.assignmentlearning.domain.model

data class Lesson(
    val id: Int,
    val courseId: Int,
    val title: String,
    val isCompleted: Boolean
)
