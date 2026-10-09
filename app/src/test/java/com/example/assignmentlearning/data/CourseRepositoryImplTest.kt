package com.example.assignmentlearning.data

import com.example.assignmentlearning.data.api.*
import com.example.assignmentlearning.data.local.*
import com.example.assignmentlearning.data.repository.CourseRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException

class CourseRepositoryImplTest {
    private val api: CourseApi = mock()
    private val dao: CourseDao = mock()
    private val tx = object : TransactionRunner {
        override suspend fun <T> run(block: suspend () -> T): T = block()
    }
    private val repository = CourseRepositoryImpl(api, dao, tx)

    @Test fun refresh_keepsLocallyCompletedLessons_andRecomputesProgress() = runTest {
        whenever(api.getCourses()).thenReturn(listOf(CourseDto(1, "Python", "John", 0, 2)))
        whenever(api.getLessons(1)).thenReturn(listOf(LessonDto(1000, "Intro", false), LessonDto(1001, "Vars", false)))
        whenever(dao.getAllLessons()).thenReturn(listOf(LessonEntity(1000, 1, "Intro", 0, completed = true)))

        assertTrue(repository.refreshCourses().isSuccess)

        val lessons = argumentCaptor<List<LessonEntity>>().also { verify(dao).upsertLessons(it.capture()) }
        assertEquals(listOf(true, false), lessons.firstValue.map { it.completed })
        val courses = argumentCaptor<List<CourseEntity>>().also { verify(dao).upsertCourses(it.capture()) }
        assertEquals(50, courses.firstValue.single().progress)
    }

    @Test fun refresh_networkFailure_returnsFailure_andLeavesCacheUntouched() = runTest {
        whenever(api.getCourses()).thenAnswer { throw IOException("No internet connection") }

        val result = repository.refreshCourses()

        assertEquals("No internet connection", result.exceptionOrNull()?.message)
        verifyNoInteractions(dao)
    }

    @Test fun completeLesson_marksLesson_andUpdatesCourseProgress() = runTest {
        whenever(dao.completedCount(1)).thenReturn(3)
        whenever(dao.lessonCount(1)).thenReturn(4)

        repository.completeLesson(courseId = 1, lessonId = 1002)

        inOrder(dao) {
            verify(dao).markLessonCompleted(1002)
            verify(dao).updateProgress(1, 75)
        }
    }
}
