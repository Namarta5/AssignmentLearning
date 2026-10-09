package com.example.assignmentlearning.presentation.course

import com.example.assignmentlearning.domain.model.Course

sealed interface CourseUiState {
    data object Loading : CourseUiState
    data object Empty : CourseUiState
    data class Error(val message: String) : CourseUiState
    data class Success(val courses: List<Course>, val isRefreshing: Boolean, val offline: Boolean) : CourseUiState
}

