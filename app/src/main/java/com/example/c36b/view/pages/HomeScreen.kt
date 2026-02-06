package com.example.c36b.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.c36b.R
import com.example.c36b.view.components.PostCard
import androidx.compose.foundation.lazy.items
import com.example.c36b.model.Post
import androidx.compose.runtime.LaunchedEffect
import com.example.c36b.viewmodel.BookmarkViewModel

@Composable
fun HomeScreen() {

    // Fetch posts from Firebase
    val postViewModel: com.example.c36b.viewmodel.PostViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return com.example.c36b.viewmodel.PostViewModel(com.example.c36b.repository.PostRepositoryImpl()) as T
        }
    })
    val bookmarkViewModel: BookmarkViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return BookmarkViewModel(com.example.c36b.repository.BookmarkRepositoryImpl()) as T
        }
    })
    var posts by remember { mutableStateOf<List<com.example.c36b.model.Post>>(emptyList()) }
    var bookmarkedPostIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showComments by remember { mutableStateOf<Post?>(null) }
    val currentUserId = com.example.c36b.repository.UserRepositoryImpl().getCurrentUser()?.uid ?: ""

    LaunchedEffect(Unit) {
        // First, get all posts
        postViewModel.getAllPosts { result, err ->
            if (result != null) {
                posts = result
                // Then get bookmarked post IDs
                bookmarkViewModel.getBookmarkedPosts(currentUserId) { success, bookmarkedIds, message ->
                    if (success) {
                        bookmarkedPostIds = bookmarkedIds
                    }
                    isLoading = false
                }
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
    ) {
        Text(
            text = "Dainiki Homepage",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Discover amazing stories",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))
        when {
            isLoading -> {
                androidx.compose.material3.Text("Loading posts...")
            }
            error != null -> {
                androidx.compose.material3.Text("Error: $error")
            }
            else -> {
                LazyColumn {
                    items(posts) { post ->
                        val currentUserId = com.example.c36b.repository.UserRepositoryImpl().getCurrentUser()?.uid ?: ""
                        var liked by remember { mutableStateOf(post.likedBy.contains(currentUserId)) }
                        var likeCount by remember { mutableStateOf(post.likes) }
                        val isBookmarked = post.key != null && bookmarkedPostIds.contains(post.key)
                        
                        PostCard(
                            post = post.copy(likes = likeCount),
                            onLike = {
                                if (post.key != null) {
                                    if (!liked) {
                                        // Add like
                                        liked = true
                                        likeCount++
                                        postViewModel.addLike(postId = post.key!!, userId = currentUserId) { success, _ ->
                                            if (success) {
                                                postViewModel.getAllPosts { result, err ->
                                                    if (result != null) posts = result
                                                }
                                            }
                                        }
                                    } else {
                                        // Remove like
                                        liked = false
                                        likeCount--
                                        postViewModel.removeLike(postId = post.key!!, userId = currentUserId) { success, _ ->
                                            if (success) {
                                                postViewModel.getAllPosts { result, err ->
                                                    if (result != null) posts = result
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            onComment = { showComments = post },
                            liked = liked,
                            onBookmark = {
                                if (post.key != null) {
                                    if (isBookmarked) {
                                        // Remove bookmark
                                        bookmarkViewModel.removeFromBookmark(
                                            postId = post.key!!,
                                            userId = currentUserId,
                                            callback = { success, message ->
                                                if (success) {
                                                    bookmarkedPostIds = bookmarkedPostIds.filter { it != post.key }
                                                }
                                            }
                                        )
                                    } else {
                                        // Add bookmark
                                        bookmarkViewModel.addToBookmark(
                                            postId = post.key!!,
                                            userId = currentUserId,
                                            callback = { success, message ->
                                                if (success) {
                                                    bookmarkedPostIds = bookmarkedPostIds + post.key!!
                                                }
                                            }
                                        )
                                    }
                                }
                            },
                            isBookmarked = isBookmarked
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    if (showComments != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showComments = null },
            confirmButton = {},
            text = {
                com.example.c36b.view.components.CommentPage(
                    comments = showComments!!.comments,
                    onSubmit = { comment ->
                        if (comment == "__close__") {
                            showComments = null
                        } else {
                            val currentUserId = com.example.c36b.repository.UserRepositoryImpl().getCurrentUser()?.uid ?: ""
                            postViewModel.addComment(showComments!!.key!!, comment, currentUserId) { success, _ ->
                                if (success) {
                                    postViewModel.getAllPosts { result, _ ->
                                        if (result != null) posts = result
                                    }
                                }
                            }
                        }
                        showComments = null
                    }
                )
            }
        )
    }
}
