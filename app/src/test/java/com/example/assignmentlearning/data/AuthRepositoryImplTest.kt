package com.example.assignmentlearning.data

import com.example.assignmentlearning.data.api.AuthApi
import com.example.assignmentlearning.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

class AuthRepositoryImplTest {
    private val api: AuthApi = mock()
    private val repository = AuthRepositoryImpl(api)

    @Test fun apiReturnsTrue_returnsSuccess() = runTest {
        whenever(api.login("a@b.com", "123456")).thenReturn(true)
        assertTrue(repository.login("a@b.com", "123456").isSuccess)
    }

    @Test fun apiReturnsFalse_returnsInvalidCredentialsFailure() = runTest {
        whenever(api.login("a@b.com", "bad")).thenReturn(false)
        val result = repository.login("a@b.com", "bad")
        assertEquals("Invalid email or password", result.exceptionOrNull()?.message)
    }

    @Test fun apiThrows_isWrappedInFailure() = runTest {
        whenever(api.login("a@b.com", "123456")).thenAnswer { throw IOException("timeout") }
        assertEquals("timeout", repository.login("a@b.com", "123456").exceptionOrNull()?.message)
    }
}
