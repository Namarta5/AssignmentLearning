package com.example.assignmentlearning.data.api

import kotlinx.coroutines.delay
import java.io.IOException

data class CourseDto(
    val id: Int,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessons: Int
)

data class LessonDto(val id: Int, val title: String, val completed: Boolean)

interface CourseApi {
    suspend fun getCourses(): List<CourseDto>
    suspend fun getLessons(courseId: Int): List<LessonDto>
}

/** Mock API: simulates latency and throws IOException when the device is offline. */
class MockCourseApi(private val isOnline: () -> Boolean) : CourseApi {
    private val courses = listOf(
        CourseDto(1, "Python Programming", "John Smith", 65, 20),
        CourseDto(2, "Generative AI", "Sarah Williams", 40, 16),
        CourseDto(3, "Full Stack Development", "David Brown", 25, 28),
    )

    override suspend fun getCourses(): List<CourseDto> {
        simulateNetwork(); return courses
    }

    override suspend fun getLessons(courseId: Int): List<LessonDto> {
        simulateNetwork()

        val course = courses.first { it.id == courseId }
        val completedLessons =
            (course.progress * course.lessons) / 100

        val lessonNames = listOf(
            "Introduction",
            "Variables & Data Types",
            "Functions",
            "OOP"
        )

        val lessons = mutableListOf<LessonDto>()

        for (i in 0 until course.lessons) {
            val title = if (i < lessonNames.size) {
                lessonNames[i]
            } else {
                "Lesson ${i + 1}"
            }
            lessons.add(
                LessonDto(
                    id = courseId * 1000 + i,
                    title = title,
                    completed = i < completedLessons
                )
            )
        }

        return lessons
    }

    private suspend fun simulateNetwork() {
        delay(800)
        if (!isOnline()) throw IOException("No internet connection")
    }
}
