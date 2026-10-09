package com.example.assignmentlearning.di

import android.content.Context
import androidx.room.Room
import com.example.assignmentlearning.data.local.AppDatabase
import com.example.assignmentlearning.data.local.CourseDao
import com.example.assignmentlearning.data.local.RoomTransactionRunner
import com.example.assignmentlearning.data.local.TransactionRunner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "learning.db").build()
    }

    @Provides
    fun provideCourseDao(db: AppDatabase): CourseDao {
        return db.courseDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRunner(db: AppDatabase): TransactionRunner {
        return RoomTransactionRunner(db)
    }
}
