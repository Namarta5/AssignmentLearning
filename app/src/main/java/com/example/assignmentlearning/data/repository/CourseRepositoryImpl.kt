package com.example.assignmentlearning.data.repository

import com.example.assignmentlearning.domain.repository.CourseRepository
import com.example.assignmentlearning.data.api.CourseApi
import com.example.assignmentlearning.data.local.*
import com.example.assignmentlearning.domain.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room is the single source of truth; the network only refreshes it. */
class CourseRepositoryImpl @Inject constructor(
    private val api: CourseApi,
    private val dao: CourseDao,
    private val tx: TransactionRunner,
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> = dao.observeCourses().map { l -> l.map { it.toDomain() } }

    override fun observeCourse(id: Int): Flow<Course?> =
        combine(dao.observeCourse(id), dao.observeLessons(id)) { c, lessons ->
            c?.toDomain(lessons.map { Lesson(it.id, it.courseId, it.title, it.completed) })
        }

    override suspend fun refreshCourses(): Result<Unit> = try {
        val courses = api.getCourses()
        val remoteLessons = courses.associate { it.id to api.getLessons(it.id) } // N+1: see README (scale)
        tx.run {
            val local = dao.getAllLessons().associateBy { it.id }
            val lessons = courses.flatMap { c ->
                remoteLessons.getValue(c.id).mapIndexed { i, l ->
                    // Locally completed lessons are never lost on refresh.
                    LessonEntity(l.id, c.id, l.title, i, l.completed || local[l.id]?.completed == true)
                }
            }
            val ids = courses.map { it.id }
            dao.deleteCoursesNotIn(ids)
            dao.deleteLessonsNotIn(ids)
            dao.upsertLessons(lessons)
            dao.upsertCourses(courses.map { c ->
                val done = lessons.count { it.courseId == c.id && it.completed }
                CourseEntity(c.id, c.title, c.instructor, c.lessons, ProgressCalculator.percent(done, c.lessons))
            })
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun completeLesson(courseId: Int, lessonId: Int) {
        tx.run {
            dao.markLessonCompleted(lessonId)
            dao.updateProgress(courseId, ProgressCalculator.percent(dao.completedCount(courseId), dao.lessonCount(courseId)))
        }
    }

    private fun CourseEntity.toDomain(lessons: List<Lesson> = emptyList()) =
        Course(id, title, instructor, lessonCount, progress, lessons)
}
