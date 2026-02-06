package com.example.c36b.repository

class BookmarkRepositoryImpl: BookmarkRepository {
    private val database = com.google.firebase.database.FirebaseDatabase.getInstance()
    private val ref = database.reference.child("bookmarks")

    override fun addToBookmarks(
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ref.child(userId).child(postId).setValue(true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Unknown error") }
    }

    override fun removeFromBookmarks(
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ref.child(userId).child(postId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Unknown error") }
    }

    override fun getBookmarkedPosts(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        ref.child(userId).get()
            .addOnSuccessListener { snapshot ->
                val bookmarkedPostIds = mutableListOf<String>()
                for (child in snapshot.children) {
                    if (child.value == true) {
                        bookmarkedPostIds.add(child.key ?: "")
                    }
                }
                onSuccess(bookmarkedPostIds)
            }
            .addOnFailureListener { onError(it.message ?: "Unknown error") }
    }
}