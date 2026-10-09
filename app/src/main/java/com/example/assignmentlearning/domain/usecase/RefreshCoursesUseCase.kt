package com.example.assignmentlearning.domain.usecase

import com.example.assignmentlearning.domain.repository.CourseRepository
import javax.inject.Inject

class RefreshCoursesUseCase @Inject constructor(private val repository: CourseRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshCourses()
}
