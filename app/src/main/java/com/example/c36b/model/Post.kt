package com.example.c36b.model

import android.net.Uri

data class Post(
    val key: String? = null,
    val username: String="",
    val time: String="",
    val title: String="",
    val content: String="",
    val tags: List<String> = emptyList(),
    val likes: Int=0,
    val likedBy: List<String> = emptyList(),
    val commentsCount: Int=0,
    val comments: List<String> = emptyList(),
    val shares: Int=0,
    val imageUrl: String? = null,
    val imageUri: Uri? = null
)
