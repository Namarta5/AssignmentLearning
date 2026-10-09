package com.example.assignmentlearning.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.assignmentlearning.data.api.AuthApi
import com.example.assignmentlearning.data.api.CourseApi
import com.example.assignmentlearning.data.api.MockAuthApi
import com.example.assignmentlearning.data.api.MockCourseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApi(): AuthApi {
        return MockAuthApi()
    }

    @Provides
    @Singleton
    fun provideCourseApi(@ApplicationContext context: Context): CourseApi {
        return MockCourseApi {
            val cm = context.getSystemService(ConnectivityManager::class.java)
            val caps = cm.getNetworkCapabilities(cm.activeNetwork)
            caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
    }
}
