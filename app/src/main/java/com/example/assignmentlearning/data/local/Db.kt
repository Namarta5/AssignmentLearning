package com.example.assignmentlearning.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "courses")
data class CourseEntity(@PrimaryKey val id: Int, val title: String, val instructor: String, val lessonCount: Int, val progress: Int)

@Entity(tableName = "lessons", indices = [Index("courseId")])
data class LessonEntity(@PrimaryKey val id: Int, val courseId: Int, val title: String, val orderIndex: Int, val completed: Boolean)

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY id") fun observeCourses(): Flow<List<CourseEntity>>
    @Query("SELECT * FROM courses WHERE id = :id") fun observeCourse(id: Int): Flow<CourseEntity?>
    @Query("SELECT * FROM lessons WHERE courseId = :id ORDER BY orderIndex") fun observeLessons(id: Int): Flow<List<LessonEntity>>
    @Query("SELECT * FROM lessons") suspend fun getAllLessons(): List<LessonEntity>
    @Upsert suspend fun upsertCourses(items: List<CourseEntity>)
    @Upsert suspend fun upsertLessons(items: List<LessonEntity>)
    @Query("DELETE FROM courses WHERE id NOT IN (:ids)") suspend fun deleteCoursesNotIn(ids: List<Int>)
    @Query("DELETE FROM lessons WHERE courseId NOT IN (:ids)") suspend fun deleteLessonsNotIn(ids: List<Int>)
    @Query("UPDATE lessons SET completed = 1 WHERE id = :id") suspend fun markLessonCompleted(id: Int)
    @Query("UPDATE courses SET progress = :progress WHERE id = :id") suspend fun updateProgress(id: Int, progress: Int)
    @Query("SELECT COUNT(*) FROM lessons WHERE courseId = :id AND completed = 1") suspend fun completedCount(id: Int): Int
    @Query("SELECT COUNT(*) FROM lessons WHERE courseId = :id") suspend fun lessonCount(id: Int): Int
}

@Database(entities = [CourseEntity::class, LessonEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() { abstract fun courseDao(): CourseDao }

/** Abstraction over Room transactions so the repository can be unit-tested with plain mocks. */
interface TransactionRunner { suspend fun <T> run(block: suspend () -> T): T }

class RoomTransactionRunner(private val db: AppDatabase) : TransactionRunner {
    override suspend fun <T> run(block: suspend () -> T): T = db.withTransaction { block() }
}
