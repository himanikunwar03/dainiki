package com.example.c36b.repository

import com.example.c36b.model.Post

interface BookmarkRepository {
    fun addToBookmarks(postId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    fun removeFromBookmarks(postId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit)
    fun getBookmarkedPosts(userId: String, onSuccess: (List<String>) -> Unit, onError: (String) -> Unit)
}