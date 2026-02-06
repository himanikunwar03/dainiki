package com.example.c36b

import com.example.c36b.repository.UserAuthRepository
import com.example.c36b.repository.UserAuthRepositoryImpl
import com.example.c36b.repository.UserRepositoryImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class UserAuthUnitTest {
    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    private lateinit var AuthRepo: UserAuthRepositoryImpl

    @Captor
    private lateinit var captor: ArgumentCaptor<OnCompleteListener<AuthResult>>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        AuthRepo = UserAuthRepositoryImpl(mockAuth)
    }
    @Test
    fun testLogin_Successful() {
        val email = "test@example.com"
        val password = "testPassword"
        var expectedResult = "Initial Value" // Define the initial value

        // Mocking task to simulate successful registration
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.signInWithEmailAndPassword(any(), any()))
            .thenReturn(mockTask)

        // Define a callback that updates the expectedResult
        val callback = { success: Boolean, message: String? ->
            expectedResult = message ?: "Callback message is null"
        }

        // Call the function under test
        AuthRepo.login(email, password, callback)

        verify(mockTask).addOnCompleteListener(captor.capture())
        captor.value.onComplete(mockTask)

        // Assert the result
        assertEquals("Login successfully", expectedResult)
    }

    @Test
    fun testLogin_Failed() {
        val email = "test@example.com"
        val password = "wrongPassword"
        var expectedResult = "Initial Value"

        // Mocking task to simulate failed login
        `when`(mockTask.isSuccessful).thenReturn(false)
        `when`(mockTask.exception).thenReturn(Exception("Invalid email or password"))
        `when`(mockAuth.signInWithEmailAndPassword(any(), any()))
            .thenReturn(mockTask)

        // Define a callback that updates the expectedResult
        val callback = { success: Boolean, message: String? ->
            expectedResult = message ?: "Callback message is null"
        }

        // Call the function under test
        AuthRepo.login(email, password, callback)

        verify(mockTask).addOnCompleteListener(captor.capture())
        captor.value.onComplete(mockTask)

        // Assert the result
        assertEquals("Invalid email or password", expectedResult)
    }

    @Test
    fun testRegister_Successful() {
        val email = "test@example.com"
        val password = "testPassword123"
        var expectedSuccess = false
        var expectedMessage = "Initial Value"
        var expectedUserId = "Initial User ID"

        // Mocking task to simulate successful registration
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(mockTask)

        // Mock current user to return a test user ID
        val mockUser = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn("testUserId123")
        `when`(mockAuth.currentUser).thenReturn(mockUser)

        // Define a callback that updates the expected values
        val callback = { success: Boolean, message: String, userId: String ->
            expectedSuccess = success
            expectedMessage = message
            expectedUserId = userId
        }

        // Call the function under test
        AuthRepo.register(email, password, callback)

        verify(mockTask).addOnCompleteListener(captor.capture())
        captor.value.onComplete(mockTask)

        // Assert the results
        assertEquals(true, expectedSuccess)
        assertEquals("Registration successfull", expectedMessage)
        assertEquals("testUserId123", expectedUserId)
    }

    @Test
    fun testRegister_Failed() {
        val email = "test@example.com"
        val password = "weak"
        var expectedSuccess = true
        var expectedMessage = "Initial Value"
        var expectedUserId = "Initial User ID"

        // Mocking task to simulate failed registration
        `when`(mockTask.isSuccessful).thenReturn(false)
        `when`(mockTask.exception).thenReturn(Exception("Password is too weak"))
        `when`(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(mockTask)

        // Define a callback that updates the expected values
        val callback = { success: Boolean, message: String, userId: String ->
            expectedSuccess = success
            expectedMessage = message
            expectedUserId = userId
        }

        // Call the function under test
        AuthRepo.register(email, password, callback)

        verify(mockTask).addOnCompleteListener(captor.capture())
        captor.value.onComplete(mockTask)

        // Assert the results
        assertEquals(false, expectedSuccess)
        assertEquals("Password is too weak", expectedMessage)
        assertEquals("", expectedUserId)
    }

}