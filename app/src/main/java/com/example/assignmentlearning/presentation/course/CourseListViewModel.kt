package com.example.assignmentlearning.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlearning.domain.usecase.GetCoursesUseCase
import com.example.assignmentlearning.domain.usecase.RefreshCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


private sealed interface RefreshState {
    data object Loading : RefreshState
    data object Done : RefreshState
    data class Failed(val message: String) : RefreshState
}

@HiltViewModel
class CourseViewModel @Inject constructor(
    getCourses: GetCoursesUseCase,
    private val refreshCourses: RefreshCoursesUseCase,
) : ViewModel() {

    private val refreshState = MutableStateFlow<RefreshState>(RefreshState.Loading)

    val state: StateFlow<CourseUiState> = combine(getCourses(), refreshState) { courses, refresh ->
        when {
            courses.isNotEmpty() -> CourseUiState.Success(courses, refresh is RefreshState.Loading, refresh is RefreshState.Failed)
            refresh is RefreshState.Loading -> CourseUiState.Loading
            refresh is RefreshState.Failed -> CourseUiState.Error(refresh.message)
            else -> CourseUiState.Empty
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CourseUiState.Loading)

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            refreshState.value = RefreshState.Loading
            refreshState.value = refreshCourses().fold(
                onSuccess = { RefreshState.Done },
                onFailure = { RefreshState.Failed(it.message ?: "Something went wrong") },
            )
        }
    }
}
