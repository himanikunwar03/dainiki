package com.example.c36b.viewmodel

import androidx.lifecycle.ViewModel
import com.example.c36b.repository.BookmarkRepository

class BookmarkViewModel(val repo: BookmarkRepository): ViewModel() {
    fun addToBookmark(
        postId: String,
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addToBookmarks(
            postId,
            userId,
            onSuccess = {
                callback(true, "Bookmark added successfully")
            },
            onError = { errorMessage ->
                callback(false, errorMessage)
            }
        )
    }

    fun removeFromBookmark(
        postId: String,
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.removeFromBookmarks(
            postId,
            userId,
            onSuccess = {
                callback(true, "Bookmark removed successfully")
            },
            onError = { errorMessage ->
                callback(false, errorMessage)
            }
        )
    }

    fun getBookmarkedPosts(
        userId: String,
        callback: (Boolean, List<String>, String) -> Unit
    ) {
        repo.getBookmarkedPosts(
            userId,
            onSuccess = { bookmarkedPostIds ->
                callback(true, bookmarkedPostIds, "Bookmarks fetched successfully")
            },
            onError = { errorMessage ->
                callback(false, emptyList(), errorMessage)
            }
        )
    }
}