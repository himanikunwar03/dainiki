package com.example.c36b.viewmodel

import androidx.lifecycle.ViewModel
import com.example.c36b.model.Post
import com.example.c36b.repository.PostRepository
import android.net.Uri

class PostViewModel(private val repo: PostRepository) : ViewModel() {
    fun createPost(post: Post, callback: (Boolean, String) -> Unit) {
        repo.createPost(post, callback)
    }

    fun getAllPosts(callback: (List<Post>?, String?) -> Unit) {
        repo.getAllPosts(callback)
    }

    fun getPostById(postId: String, callback: (Post?, String?) -> Unit) {
        repo.getPostById(postId, callback)
    }

    fun updatePost(postId: String, post: Post, callback: (Boolean, String) -> Unit) {
        repo.updatePost(postId, post, callback)
    }

    fun updatePostWithImage(context: android.content.Context, postId: String, post: Post, callback: (Boolean, String) -> Unit) {
        if (repo is com.example.c36b.repository.PostRepositoryImpl) {
            repo.updatePostWithImage(context, postId, post, callback)
        } else {
            callback(false, "Update with image not supported by this repository implementation.")
        }
    }

    fun deletePost(postId: String, callback: (Boolean, String) -> Unit) {
        repo.deletePost(postId, callback)
    }

    fun getPostsByUser(username: String, callback: (List<Post>?, String?) -> Unit) {
        repo.getPostsByUser(username, callback)
    }

    fun getPostsByTag(tag: String, callback: (List<Post>?, String?) -> Unit) {
        repo.getPostsByTag(tag, callback)
    }

    fun uploadImageToCloudinary(context: android.content.Context, imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        if (repo is com.example.c36b.repository.PostRepositoryImpl) {
            repo.uploadImageToCloudinary(context, imageUri, onResult)
        } else {
            onResult(false, "Upload not supported by this repository implementation.")
        }
    }

    fun addLike(postId: String, userId: String, callback: (Boolean, String) -> Unit) {
        if (repo is com.example.c36b.repository.PostRepositoryImpl) {
            repo.addLike(postId, userId, callback)
        } else {
            callback(false, "Like not supported by this repository implementation.")
        }
    }

    fun removeLike(postId: String, userId: String, callback: (Boolean, String) -> Unit) {
        if (repo is com.example.c36b.repository.PostRepositoryImpl) {
            repo.removeLike(postId, userId, callback)
        } else {
            callback(false, "Remove like not supported by this repository implementation.")
        }
    }

    fun addComment(
        postId: String,
        comment: String,
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addComment(postId, comment, userId, callback)
    }
}
