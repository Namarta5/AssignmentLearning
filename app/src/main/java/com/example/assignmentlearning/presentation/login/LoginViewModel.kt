package com.example.assignmentlearning.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlearning.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())

    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {

        _state.value = _state.value.copy(
            email = email,
            emailError = null,
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {

        _state.value = _state.value.copy(
            password = password,
            passwordError = null,
            errorMessage = null
        )
    }

    fun login() {

        val currentState = _state.value
        if (currentState.isLoading) return

        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {

            _state.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError,
                isLoginSuccess = false,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {

            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = loginUseCase(
                email = currentState.email.trim(),
                password = currentState.password
            )

            result.onSuccess {
                _state.value = currentState.copy(
                    isLoading = false,
                    isLoginSuccess = true
                )

            }.onFailure { error ->
                _state.value = currentState.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Login failed"
                )
            }
        }
    }

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private fun validateEmail(email: String): String? {

        return when {

            email.isBlank() ->
                "Email is required"

            !emailRegex.matches(email.trim()) ->
                "Enter a valid email"

            else -> null
        }
    }

    private fun validatePassword(password: String): String? {

        return when {

            password.isBlank() ->
                "Password is required"

            password.length < 4 ->
                "Password must be at least 4 characters"

            else -> null
        }
    }
}
