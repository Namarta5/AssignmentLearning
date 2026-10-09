package com.example.assignmentlearning.domain.usecase


import com.example.assignmentlearning.domain.repository.CourseRepository
import javax.inject.Inject

class CompleteLessonUseCase @Inject constructor(private val repository: CourseRepository) {
    suspend operator fun invoke(courseId: Int, lessonId: Int) = repository.completeLesson(courseId, lessonId)
}
