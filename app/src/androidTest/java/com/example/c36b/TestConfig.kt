package com.example.c36b

/**
 * Test configuration constants and settings
 */
object TestConfig {
    
    // Test user credentials
    const val TEST_USER_EMAIL = "testuser@example.com"
    const val TEST_USER_PASSWORD = "testpassword123"
    
    // Test post data
    const val TEST_POST_TITLE = "Test Post Title"
    const val TEST_POST_CONTENT = "This is a test post content"
    
    // Test timeouts
    const val FIREBASE_TIMEOUT_MS = 10000L
    const val UI_TIMEOUT_MS = 5000L
    
    // Test tags for UI elements
    object TestTags {
        // Login screen
        const val EMAIL_FIELD = "email"
        const val PASSWORD_FIELD = "password"
        const val LOGIN_BUTTON = "submit"
        
        // Registration screen
        const val NAME_FIELD = "name"
        const val REG_EMAIL_FIELD = "reg_email"
        const val REG_PASSWORD_FIELD = "reg_password"
        const val SIGNUP_BUTTON = "signup_button"
        
        // Post screen
        const val TITLE_INPUT = "title_input"
        const val CONTENT_INPUT = "content_input"
        const val PUBLISH_BUTTON = "publish_button"
        const val DELETE_BUTTON = "delete_button"
    }
    
    // Test messages
    object TestMessages {
        const val SUCCESS_LOGIN = "Login successful"
        const val SUCCESS_REGISTRATION = "Registration successful"
        const val SUCCESS_POST_CREATED = "Post created successfully"
        const val SUCCESS_POST_DELETED = "Post deleted successfully"
        
        const val ERROR_INVALID_EMAIL = "Invalid email format"
        const val ERROR_INVALID_CREDENTIALS = "Invalid email or password"
    }
} 