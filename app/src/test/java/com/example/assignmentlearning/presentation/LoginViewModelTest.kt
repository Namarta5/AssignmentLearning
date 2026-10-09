package com.example.assignmentlearning.presentation

import com.example.assignmentlearning.domain.usecase.LoginUseCase
import com.example.assignmentlearning.presentation.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val loginUseCase: LoginUseCase = mock()
    private lateinit var viewModel: LoginViewModel

    @Before fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LoginViewModel(loginUseCase)
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun invalidInput_showsFieldErrors_andNeverCallsUseCase() = runTest {
        viewModel.onEmailChanged("not-an-email")
        viewModel.onPasswordChanged("1")
        viewModel.login()

        val s = viewModel.state.value
        assertEquals("Enter a valid email", s.emailError)
        assertEquals("Password must be at least 4 characters", s.passwordError)
        assertFalse(s.isLoading)
        verifyNoInteractions(loginUseCase)
    }

    @Test fun validCredentials_success_setsLoginSuccess() = runTest {
        whenever(loginUseCase("demo@gmail.com", "password123")).thenReturn(Result.success(Unit))
        viewModel.onEmailChanged("demo@gmail.com")
        viewModel.onPasswordChanged("password123")

        viewModel.login()

        val s = viewModel.state.value
        assertTrue(s.isLoginSuccess)
        assertFalse(s.isLoading)
        assertNull(s.errorMessage)
        verify(loginUseCase).invoke("demo@gmail.com", "password123")
    }

    @Test fun apiFailure_showsErrorMessage_andKeepsEnteredInput() = runTest {
        whenever(loginUseCase(any(), any())).thenReturn(Result.failure(Exception("Invalid email or password")))
        viewModel.onEmailChanged("demo@gmail.com")
        viewModel.onPasswordChanged("wrong-pass")

        viewModel.login()

        val s = viewModel.state.value
        assertEquals("Invalid email or password", s.errorMessage)
        assertFalse(s.isLoginSuccess)
        assertFalse(s.isLoading)
        assertEquals("demo@gmail.com", s.email) // regression: old copy(currentState) wiped later edits
    }
}
