package com.example.assignmentlearning.data.repository

import com.example.assignmentlearning.data.api.AuthApi
import com.example.assignmentlearning.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {

        return try {
            val isSuccess = authApi.login(email = email, password = password)
            if (isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
