package com.example.c36b.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c36b.model.Post
import com.example.c36b.view.components.PostCard
import com.example.c36b.viewmodel.BookmarkViewModel

@Composable
fun BookmarkScreen() {
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
    
    var allPosts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var bookmarkedPostIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var bookmarkedPosts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showComments by remember { mutableStateOf<Post?>(null) }
    
    val currentUserId = com.example.c36b.repository.UserRepositoryImpl().getCurrentUser()?.uid ?: ""

    LaunchedEffect(Unit) {
        // First, get all posts
        postViewModel.getAllPosts { result, err ->
            if (result != null) {
                allPosts = result
                // Then get bookmarked post IDs
                bookmarkViewModel.getBookmarkedPosts(currentUserId) { success, bookmarkedIds, message ->
                    if (success) {
                        bookmarkedPostIds = bookmarkedIds
                        // Filter posts to show only bookmarked ones
                        bookmarkedPosts = allPosts.filter { post -> 
                            post.key != null && bookmarkedIds.contains(post.key)
                        }
                    } else {
                        error = message
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
            .background(color = Color.Black)
            .padding(16.dp)
    ) {
        Text(
            "Bookmarked Posts", 
            color = Color.White, 
            fontSize = 24.sp, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (error != null) {
            Text("Error: $error", color = Color.Red)
        } else if (bookmarkedPosts.isEmpty()) {
            Text(
                "No bookmarks yet.", 
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(bookmarkedPosts) { post ->
                    val currentUserId = com.example.c36b.repository.UserRepositoryImpl().getCurrentUser()?.uid ?: ""
                    var liked by remember { mutableStateOf(post.likedBy.contains(currentUserId)) }
                    var likeCount by remember { mutableStateOf(post.likes) }
                    var isBookmarked by remember { mutableStateOf(true) } // Always true in bookmark screen
                    
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
                                                if (result != null) {
                                                    allPosts = result
                                                    bookmarkedPosts = allPosts.filter { p -> 
                                                        p.key != null && bookmarkedPostIds.contains(p.key)
                                                    }
                                                }
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
                                                if (result != null) {
                                                    allPosts = result
                                                    bookmarkedPosts = allPosts.filter { p -> 
                                                        p.key != null && bookmarkedPostIds.contains(p.key)
                                                    }
                                                }
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
                                bookmarkViewModel.removeFromBookmark(
                                    postId = post.key!!,
                                    userId = currentUserId,
                                    callback = { success, message ->
                                        if (success) {
                                            // Remove from bookmarked posts list
                                            bookmarkedPosts = bookmarkedPosts.filter { it.key != post.key }
                                            bookmarkedPostIds = bookmarkedPostIds.filter { it != post.key }
                                        }
                                    }
                                )
                            }
                        },
                        isBookmarked = true
                    )
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
                                        if (result != null) {
                                            allPosts = result
                                            bookmarkedPosts = allPosts.filter { post -> 
                                                post.key != null && bookmarkedPostIds.contains(post.key)
                                            }
                                        }
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