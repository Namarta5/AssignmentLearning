package com.example.assignmentlearning.domain.repository

import com.example.assignmentlearning.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    // Course list screen
    fun observeCourses(): Flow<List<Course>>

    // Course details screen
    fun observeCourse(id: Int): Flow<Course?>

    suspend fun refreshCourses(): Result<Unit>

    suspend fun completeLesson(
        courseId: Int,
        lessonId: Int
    )
}