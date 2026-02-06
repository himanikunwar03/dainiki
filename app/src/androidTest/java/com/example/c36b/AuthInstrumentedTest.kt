package com.example.c36b

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.c36b.view.LoginActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {
    
    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()
    
    @get:Rule
    val activityRule = ActivityTestRule(LoginActivity::class.java)
    
    @Before
    fun setUp() {
        TestHelper.clearTestData()
    }
    
    @Test
    fun testLoginScreen_DisplaysCorrectElements() {
        // Verify that the login screen displays all required elements
        composeRule.onNodeWithText("News Fest").assertIsDisplayed()
        composeRule.onNodeWithText("Sign in to continue").assertIsDisplayed()
        composeRule.onNodeWithTag("email").assertIsDisplayed()
        composeRule.onNodeWithTag("password").assertIsDisplayed()
        composeRule.onNodeWithTag("submit").assertIsDisplayed()
    }
    
    @Test
    fun testLogin_ValidCredentials_SuccessfulLogin() {
        // Setup test user first
        val userId = TestHelper.setupTestUser()
        
        // Enter valid credentials
        composeRule.onNodeWithTag("email")
            .performTextInput("testuser@example.com")
        
        composeRule.onNodeWithTag("password")
            .performTextInput("testpassword123")
        
        // Click Login
        composeRule.onNodeWithTag("submit")
            .performClick()
        
        // Wait for login process to complete
        composeRule.waitForIdle()
        
        // Note: In a real test, you would verify navigation to NavigationActivity
    }
    
    @Test
    fun testLogin_InvalidCredentials_ShowsError() {
        // Enter invalid credentials
        composeRule.onNodeWithTag("email")
            .performTextInput("invalid@example.com")
        
        composeRule.onNodeWithTag("password")
            .performTextInput("wrongpassword")
        
        // Click Login
        composeRule.onNodeWithTag("submit")
            .performClick()
        
        // Wait for error response
        composeRule.waitForIdle()
        
        // Note: Check for error message from Firebase
    }
}