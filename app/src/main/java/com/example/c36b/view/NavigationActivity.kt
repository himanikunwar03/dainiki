package com.example.c36b.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.c36b.view.pages.HomeScreen
import com.example.c36b.view.pages.ProfileScreen
import com.example.c36b.view.pages.BookmarkScreen
import com.example.c36b.view.pages.EditPostScreen
import com.example.c36b.R
import com.example.c36b.model.Post
import com.example.c36b.view.pages.CreateNewPost


class NavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.Black.toArgb()))
        setContent {
            NavigationBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBody() {
    data class BottomNavItem(val label: String, val icon: ImageVector)

    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("create", Icons.Default.Create),
        BottomNavItem("Bookmark", Icons.Default.FavoriteBorder),
        BottomNavItem("Profile", Icons.Default.Person)
    )

    var selectedIndex by remember { mutableStateOf(0) }
    var userModel: com.example.c36b.model.UserModel? = null
    var selectedPostId by remember { mutableStateOf<String?>(null) }
    var selectedPost by remember { mutableStateOf<com.example.c36b.model.Post?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon,
                            contentDescription = item.label) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> CreateNewPost()
                2 -> BookmarkScreen()
                3 -> {
                    // Fetch user info from UserRepository and pass to ProfileScreen
                    val userRepo = com.example.c36b.repository.UserRepositoryImpl()
                    val firebaseUser = userRepo.getCurrentUser()
                    var isLoading by remember { mutableStateOf(true) }
                    var error by remember { mutableStateOf<String?>(null) }
                    androidx.compose.runtime.LaunchedEffect(firebaseUser) {
                        if (firebaseUser != null) {
                            userRepo.getUserFromDatabase(firebaseUser.uid) { success, _, user ->
                                if (success && user != null) {
                                    userModel = user
                                    isLoading = false
                                } else {
                                    error = "Failed to load user info"
                                    isLoading = false
                                }
                            }
                        } else {
                            error = "User not logged in"
                            isLoading = false
                        }
                    }
                    when {
                        isLoading -> androidx.compose.material3.Text("Loading profile...")
                        error != null -> androidx.compose.material3.Text("Error: $error")
                        userModel != null ->                         com.example.c36b.view.pages.ProfileScreen(
                            user = userModel!!,
                            onEditProfile = { selectedIndex = 4 },
                            onSettings = { selectedIndex = 5 },
                            onCreatePost = {},
                            onPostClick = { post ->
                                selectedPost = post
                                selectedIndex = 7
                            }
                        )
                    }
                }
            }
        }
        // Add navigation for EditProfileScreen and SettingsScreen
        if (selectedIndex == 4 && userModel != null) {
            com.example.c36b.view.pages.EditProfileScreen(userModel!!)
        }
        if (selectedIndex == 5) {
            com.example.c36b.view.pages.SettingsScreen()
        }
        if (selectedIndex == 6) {
            if (selectedPostId.isNullOrEmpty()) {
                Text("No post selected or invalid post.")
            } else {
                var post by remember { mutableStateOf<com.example.c36b.model.Post?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }
                val postRepo = com.example.c36b.repository.PostRepositoryImpl()
                androidx.compose.runtime.LaunchedEffect(selectedPostId) {
                    postRepo.getPostById(selectedPostId!!) { result, err ->
                        if (result != null) {
                            post = result
                            isLoading = false
                        } else {
                            error = err
                            isLoading = false
                        }
                    }
                }
                when {
                    isLoading -> Text("Loading post...")
                    error != null -> Text("Error: $error")
                    post != null -> com.example.c36b.view.pages.PostScreen(post!!, onLike = {}, onComment = {})
                    else -> Text("Post not found.")
                }
            }
        }
        if (selectedIndex == 7) {
            if (selectedPost != null) {
                com.example.c36b.view.pages.EditPostScreen(
                    post = selectedPost!!,
                    onBack = { selectedIndex = 3 }, // Go back to profile
                    onPostUpdated = { selectedIndex = 3 }, // Go back to profile after update
                    onPostDeleted = { selectedIndex = 3 } // Go back to profile after delete
                )
            } else {
                Text("No post selected for editing.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavPreview() {
    NavigationBody()
}