package com.example.c36b.view.pages

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.c36b.model.UserModel
import com.example.c36b.model.Post
import com.example.c36b.repository.PostRepositoryImpl
import com.example.c36b.viewmodel.PostViewModel
import com.example.c36b.view.components.PostCard

@Composable
fun ProfileScreen(
    user: UserModel,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onCreatePost: () -> Unit,
    onPostClick: (Post) -> Unit
) {
    val postViewModel: PostViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return PostViewModel(PostRepositoryImpl()) as T
        }
    })
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user.name) {
        postViewModel.getPostsByUser(user.name) { result, err ->
            if (result != null) {
                posts = result
                isLoading = false
            } else {
                error = err
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    )
    {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onSettings() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Profile picture
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (user.profileImageUrl.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    tint = Color.Gray
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(user.profileImageUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Username
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        // Bio
        if (user.bio.isNotBlank()) {
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            StatItem(count = posts.size, label = "Posts")
            StatItem(count = user.followersCount, label = "Followers")
            StatItem(count = user.followingCount, label = "Following")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Edit Profile button
        Button(
            onClick = onEditProfile,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text("Edit Profile")
        }
        Spacer(modifier = Modifier.height(24.dp))
        // User's posts
        when {
            isLoading -> {
                Text("Loading posts...", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error != null -> {
                Text("Error: $error", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            posts.isEmpty() -> {
                Button(
                    onClick = onCreatePost,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Create Post")
                }
            }
            else -> {
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp)) {
                    items(posts) { post ->
                        Box(modifier = Modifier
                            .padding(4.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { onPostClick(post) }
                        ) {
                            if (post.imageUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(post.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray)
    }
}
