package com.example.assignmentlearning.domain.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val progress: Int,
    val lessons: List<Lesson> = emptyList()
)
