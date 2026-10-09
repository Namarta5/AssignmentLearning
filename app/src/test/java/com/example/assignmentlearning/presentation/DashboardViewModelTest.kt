package com.example.assignmentlearning.presentation

import com.example.assignmentlearning.domain.model.Course
import com.example.assignmentlearning.domain.usecase.GetCoursesUseCase
import com.example.assignmentlearning.domain.usecase.RefreshCoursesUseCase
import com.example.assignmentlearning.presentation.course.CourseUiState
import com.example.assignmentlearning.presentation.course.CourseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CourseViewModelTest {
    private val getCourses: GetCoursesUseCase = mock()
    private val refreshCourses: RefreshCoursesUseCase = mock()
    private val course = Course(1, "Python", "John", 20, 65)

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private suspend fun TestScope.createState(cached: List<Course>, refresh: Result<Unit>): CourseUiState {
        whenever(getCourses()).thenReturn(MutableStateFlow(cached))
        whenever(refreshCourses()).thenReturn(refresh)
        val vm = CourseViewModel(getCourses, refreshCourses)
        backgroundScope.launch { vm.state.collect {} }
        return vm.state.value
    }

    /** Core offline guarantee: a failed refresh must not hide previously cached courses. */
    @Test fun refreshFails_butCacheExists_showsCachedCoursesAsOffline() = runTest {
        val s = createState(listOf(course), Result.failure(IOException("No internet connection")))
        assertTrue(s is CourseUiState.Success)
        s as CourseUiState.Success
        assertTrue(s.offline)
        assertEquals(listOf(course), s.courses)
    }

    @Test fun refreshFails_noCache_showsError() = runTest {
        val s = createState(emptyList(), Result.failure(IOException("No internet connection")))
        assertEquals(CourseUiState.Error("No internet connection"), s)
    }

    @Test fun refreshSucceeds_noCourses_showsEmpty() = runTest {
        assertEquals(CourseUiState.Empty, createState(emptyList(), Result.success(Unit)))
    }

    @Test fun refreshSucceeds_withCourses_showsSuccessOnline() = runTest {
        val s = createState(listOf(course), Result.success(Unit))
        assertEquals(CourseUiState.Success(listOf(course), isRefreshing = false, offline = false), s)
        verify(refreshCourses).invoke()
    }
}
