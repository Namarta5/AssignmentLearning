package com.example.assignmentlearning.data.api

import kotlinx.coroutines.delay

class MockAuthApi : AuthApi {
    override suspend fun login(email: String, password: String): Boolean {
        delay(1000)
        return email.trim().equals(DEMO_EMAIL, ignoreCase = true) && password == DEMO_PASSWORD
    }

    companion object {
        const val DEMO_EMAIL = "demo@gmail.com"
        const val DEMO_PASSWORD = "password123"
    }
}
