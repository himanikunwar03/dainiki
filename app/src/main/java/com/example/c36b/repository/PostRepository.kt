package com.example.c36b.repository

import com.example.c36b.model.Post

interface PostRepository {
    fun createPost(post: Post, callback: (Boolean, String) -> Unit)
    fun getAllPosts(callback: (List<Post>?, String?) -> Unit)
    fun getPostById(postId: String, callback: (Post?, String?) -> Unit)
    fun updatePost(postId: String, post: Post, callback: (Boolean, String) -> Unit)
    fun deletePost(postId: String, callback: (Boolean, String) -> Unit)
    fun getPostsByUser(username: String, callback: (List<Post>?, String?) -> Unit)
    fun getPostsByTag(tag: String, callback: (List<Post>?, String?) -> Unit)
    fun addLike(postId: String, userId: String, callback: (Boolean, String) -> Unit)
    fun removeLike(postId: String, userId: String, callback: (Boolean, String) -> Unit)
    fun addComment(postId: String, comment: String, userId: String, callback: (Boolean, String) -> Unit)

}
