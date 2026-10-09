package com.example.assignmentlearning.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlearning.domain.model.Course
import com.example.assignmentlearning.domain.usecase.CompleteLessonUseCase
import com.example.assignmentlearning.domain.usecase.GetCourseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCourseDetail: GetCourseDetailUseCase,
    private val completeLessonUseCase: CompleteLessonUseCase,
) : ViewModel() {

    private val courseId: Int = checkNotNull(savedStateHandle["courseId"])

    val course: StateFlow<Course?> = getCourseDetail(courseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun completeLesson(lessonId: Int) {
        viewModelScope.launch { completeLessonUseCase(courseId, lessonId) }
    }
}
