package com.example.assignmentlearning.domain.usecase

import com.example.assignmentlearning.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email:String, password: String): Result<Unit> = repository.login(email,password)
}
