package com.example.assignmentlearning.domain.usecase

import com.example.assignmentlearning.domain.repository.CourseRepository
import com.example.assignmentlearning.domain.model.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCourseDetailUseCase @Inject constructor(private val repository: CourseRepository) {
    operator fun invoke(courseId: Int): Flow<Course?> = repository.observeCourse(courseId)
}
