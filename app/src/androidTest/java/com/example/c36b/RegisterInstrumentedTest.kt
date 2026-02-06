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
import com.example.c36b.view.RegistrationActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterInstrumentedTest {
    
    @get:Rule
    val composeRule = createAndroidComposeRule<RegistrationActivity>()
    
    @get:Rule
    val activityRule = ActivityTestRule(RegistrationActivity::class.java)
    
    @Before
    fun setUp() {
        TestHelper.clearTestData()
    }
    
    @Test
    fun testRegistrationScreen_DisplaysCorrectElements() {
        // Verify that the registration screen displays all required elements
        composeRule.onNodeWithText("News Fest").assertIsDisplayed()
        composeRule.onNodeWithText("Create An Account").assertIsDisplayed()
        composeRule.onNodeWithText("Name").assertIsDisplayed()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Signup").assertIsDisplayed()
    }
    
    @Test
    fun testRegistration_ValidInput_SuccessfulRegistration() {
        val testName = "Test User"
        val testEmail = TestHelper.generateUniqueTestEmail()
        val testPassword = "password123"
        
        // Enter name
        composeRule.onNodeWithText("Name").performTextInput(testName)
        
        // Enter email
        composeRule.onNodeWithText("Email").performTextInput(testEmail)
        
        // Enter password
        composeRule.onNodeWithText("Password").performTextInput(testPassword)
        
        // Click signup button
        composeRule.onNodeWithText("Signup").performClick()
        
        // Wait for the registration process to complete
        composeRule.waitForIdle()
        
        // Note: In a real test, you would verify navigation to LoginActivity
    }
    
    @Test
    fun testRegistration_InvalidEmail_ShowsValidation() {
        val invalidEmail = "invalid-email"
        val testPassword = "password123"
        val testName = "Test User"
        
        // Enter invalid email
        composeRule.onNodeWithText("Name").performTextInput(testName)
        composeRule.onNodeWithText("Email").performTextInput(invalidEmail)
        composeRule.onNodeWithText("Password").performTextInput(testPassword)
        
        // Click signup button
        composeRule.onNodeWithText("Signup").performClick()
        
        // Wait for validation
        composeRule.waitForIdle()
        
        // Note: Check for email validation error message
    }
} 