package com.example.assignmentlearning.domain.usecase

import com.example.assignmentlearning.domain.repository.CourseRepository
import com.example.assignmentlearning.domain.model.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(private val repository: CourseRepository) {
    operator fun invoke(): Flow<List<Course>> = repository.observeCourses()
}
