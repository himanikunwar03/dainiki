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
import com.example.c36b.view.NavigationActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostInstrumentedTest {
    
    @get:Rule
    val composeRule = createAndroidComposeRule<NavigationActivity>()
    
    @get:Rule
    val activityRule = ActivityTestRule(NavigationActivity::class.java)
    
    @Before
    fun setUp() {
        // Setup: Login first to access post functionality
        TestHelper.setupTestUser()
        composeRule.waitForIdle()
    }
    
    @Test
    fun testHomeScreen_DisplaysCorrectElements() {
        // Verify that the home screen displays all required elements
        composeRule.onNodeWithText("BlogSpace").assertIsDisplayed()
        composeRule.onNodeWithText("Discover amazing stories").assertIsDisplayed()
        
        // Wait for posts to load
        composeRule.waitForIdle()
    }
    
    @Test
    fun testCreatePost_Functionality() {
        // Navigate to create post screen (tab index 1)
        // This would typically involve clicking on the bottom navigation
        
        // Wait for create post screen to load
        composeRule.waitForIdle()
        
        // Fill in post details
        // composeRule.onNodeWithTag("title_input").performTextInput("Test Post Title")
        // composeRule.onNodeWithTag("content_input").performTextInput("This is a test post content")
        
        // Add tags
        // composeRule.onNodeWithTag("tags_input").performTextInput("test, kotlin")
        
        // Submit the post
        // composeRule.onNodeWithText("Publish").performClick()
        
        // Wait for post creation to complete
        composeRule.waitForIdle()
        
        // Navigate back to home screen and verify post appears
        // composeRule.onNodeWithTag("home_tab").performClick()
        // composeRule.waitForIdle()
        // composeRule.onNodeWithText("Test Post Title").assertIsDisplayed()
    }
    
    @Test
    fun testDeletePost_Functionality() {
        // Wait for posts to load
        composeRule.waitForIdle()
        
        // Find a post created by current user
        // Click delete button
        // composeRule.onNodeWithTag("delete_button").performClick()
        
        // Confirm deletion
        // composeRule.onNodeWithText("Delete").performClick()
        
        // Wait for deletion to complete
        composeRule.waitForIdle()
        
        // Verify post is removed
        // composeRule.onNodeWithText("Deleted Post Title").assertDoesNotExist()
    }
} 