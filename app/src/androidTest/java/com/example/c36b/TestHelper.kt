package com.example.c36b

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth

/**
 * Helper class for instrumented tests
 * Provides common utilities and setup methods
 */
object TestHelper {
    
    private const val TEST_USER_EMAIL = "testuser@example.com"
    private const val TEST_USER_PASSWORD = "testpassword123"
    
    /**
     * Get the test context
     */
    fun getTestContext(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
    
    /**
     * Clear all test data and preferences
     */
    fun clearTestData() {
        val context = getTestContext()
        
        // Clear SharedPreferences
        val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        
        // Clear Firebase Auth (if signed in)
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.let {
            auth.signOut()
        }
    }
    
    /**
     * Setup test user authentication
     */
    fun setupTestUser(): String? {
        val auth = FirebaseAuth.getInstance()
        
        // Try to sign in with test credentials
        return try {
            val result = auth.signInWithEmailAndPassword(TEST_USER_EMAIL, TEST_USER_PASSWORD).result
            result.user?.uid
        } catch (e: Exception) {
            // If user doesn't exist, create one
            try {
                val result = auth.createUserWithEmailAndPassword(TEST_USER_EMAIL, TEST_USER_PASSWORD).result
                result.user?.uid
            } catch (createException: Exception) {
                null
            }
        }
    }
    
    /**
     * Generate unique test email
     */
    fun generateUniqueTestEmail(): String {
        val timestamp = System.currentTimeMillis()
        return "testuser$timestamp@example.com"
    }
} 