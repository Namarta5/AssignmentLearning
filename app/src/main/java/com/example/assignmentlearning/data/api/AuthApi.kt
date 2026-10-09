package com.example.assignmentlearning.data.api

interface AuthApi {
    suspend fun login(email: String, password: String): Boolean
}
