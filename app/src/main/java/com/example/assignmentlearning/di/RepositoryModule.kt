package com.example.assignmentlearning.di

import com.example.assignmentlearning.domain.repository.CourseRepository
import com.example.assignmentlearning.data.repository.AuthRepositoryImpl
import com.example.assignmentlearning.data.repository.CourseRepositoryImpl
import com.example.assignmentlearning.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRepositoryImpl(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideCourseRepository(courseRepositoryImpl: CourseRepositoryImpl): CourseRepository
}
