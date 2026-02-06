package com.example.c36b.repository

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.c36b.model.Post
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream
import java.util.concurrent.Executors
import android.os.Handler


class PostRepositoryImpl : PostRepository {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("posts")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "doayfq3xr",
            "api_key" to "652925374972233",
            "api_secret" to "lGt49TnaPIIrdJ_8tG_EJ8AHY1E"
        )
    )

    fun createPost(context: Context, post: Post, callback: (Boolean, String) -> Unit) {
        // Upload image to Cloudinary first
        if (post.imageUri == null) {
            callback(false, "Image URI is required")
            return
        }
        uploadImageToCloudinary(context, post.imageUri!!, onResult = { success, imageUrl ->
            if (success && imageUrl != null) {
                // Set the imageUrl in the post object, but remove imageUri for Firebase
                val postWithImageUrl = post.copy(imageUrl = imageUrl, imageUri = null)
                // Generate a new key for the post
                val newPostRef = ref.push()
                newPostRef.setValue(postWithImageUrl)
                    .addOnSuccessListener { callback(true, "Post created successfully") }
                    .addOnFailureListener { e -> callback(false, e.message ?: "Failed to create post") }
            } else {
                callback(false, "Failed to upload image to Cloudinary")
            }
        })
    }

    // Old signature for interface compatibility
    override fun createPost(post: Post, callback: (Boolean, String) -> Unit) {
        callback(false, "Use createPost(context, post, callback) instead.")
    }

    override fun getAllPosts(callback: (List<Post>?, String?) -> Unit) {
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val posts = mutableListOf<Post>()
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (child in snapshot.children) {
                        val post = child.getValue(Post::class.java)
                        val key = child.key
                        if (post != null) {
                            posts.add(post.copy(key = key))
                        }
                    }
                }
                callback(posts, null)
            } else {
                callback(null, task.exception?.message ?: "Failed to fetch posts")
            }
        }
    }

    override fun getPostById(postId: String, callback: (Post?, String?) -> Unit) {
        ref.child(postId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    val post = snapshot.getValue(Post::class.java)
                    callback(post?.copy(key = postId), null)
                } else {
                    callback(null, "Post not found")
                }
            } else {
                callback(null, task.exception?.message ?: "Failed to fetch post")
            }
        }
    }

    override fun updatePost(postId: String, post: Post, callback: (Boolean, String) -> Unit) {
        // If a new imageUri is provided, upload to Cloudinary first
        if (post.imageUri != null) {
            // Assume context is required, so this method should be called from an implementation that can provide it
            callback(false, "Context required for image upload. Use updatePostWithImage(context, postId, post, callback)")
            return
        } else {
            // Remove imageUri before updating in Firebase
            val postToUpdate = post.copy(imageUri = null)
            ref.child(postId).setValue(postToUpdate)
                .addOnSuccessListener { callback(true, "Post updated successfully") }
                .addOnFailureListener { e -> callback(false, e.message ?: "Failed to update post") }
        }
    }

    fun updatePostWithImage(context: Context, postId: String, post: Post, callback: (Boolean, String) -> Unit) {
        if (post.imageUri != null) {
            uploadImageToCloudinary(context, post.imageUri) { success, imageUrl ->
                if (success && imageUrl != null) {
                    val postToUpdate = post.copy(imageUrl = imageUrl, imageUri = null)
                    ref.child(postId).setValue(postToUpdate)
                        .addOnSuccessListener { callback(true, "Post updated successfully") }
                        .addOnFailureListener { e -> callback(false, e.message ?: "Failed to update post") }
                } else {
                    callback(false, "Failed to upload new image to Cloudinary")
                }
            }
        } else {
            // No new image, just update other fields
            val postToUpdate = post.copy(imageUri = null)
            ref.child(postId).setValue(postToUpdate)
                .addOnSuccessListener { callback(true, "Post updated successfully") }
                .addOnFailureListener { e -> callback(false, e.message ?: "Failed to update post") }
        }
    }

    override fun deletePost(postId: String, callback: (Boolean, String) -> Unit) {
        ref.child(postId).removeValue()
            .addOnSuccessListener { callback(true, "Post deleted successfully") }
            .addOnFailureListener { e -> callback(false, e.message ?: "Failed to delete post") }
    }

    override fun getPostsByUser(username: String, callback: (List<Post>?, String?) -> Unit) {
        ref.orderByChild("username").equalTo(username).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val posts = mutableListOf<Post>()
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (child in snapshot.children) {
                        val post = child.getValue(Post::class.java)
                        val key = child.key
                        if (post != null) {
                            posts.add(post.copy(key = key))
                        }
                    }
                }
                callback(posts, null)
            } else {
                callback(null, task.exception?.message ?: "Failed to fetch posts by user")
            }
        }
    }

    override fun getPostsByTag(tag: String, callback: (List<Post>?, String?) -> Unit) {
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val posts = mutableListOf<Post>()
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (child in snapshot.children) {
                        val post = child.getValue(Post::class.java)
                        val key = child.key
                        if (post != null && post.tags.contains(tag)) {
                            posts.add(post.copy(key = key))
                        }
                    }
                }
                callback(posts, null)
            } else {
                callback(null, task.exception?.message ?: "Failed to fetch posts by tag")
            }
        }
    }

    fun uploadImageToCloudinary(
        context: Context,
        imageUri: Uri,
        onResult: (Boolean, String?) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                // ✅ Run UI updates on the Main Thread
                Handler(Looper.getMainLooper()).post {
                    onResult(true, imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    onResult(false,null)
                }
            }
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }


    override fun addLike(postId: String, userId: String, callback: (Boolean, String) -> Unit) {
        val postRef = ref.child(postId)
        postRef.get().addOnSuccessListener { snapshot ->
            val post = snapshot.getValue(Post::class.java)
            if (post != null) {
                if (!post.likedBy.contains(userId)) {
                    val updatedLikes = post.likes + 1
                    val updatedLikedBy = post.likedBy + userId
                    postRef.child("likes").setValue(updatedLikes)
                    postRef.child("likedBy").setValue(updatedLikedBy)
                    callback(true, "Like added successfully")
                } else {
                    callback(false, "Already liked")
                }
            } else {
                callback(false, "Post not found")
            }
        }.addOnFailureListener { e ->
            callback(false, e.message ?: "Failed to add like")
        }
    }

    override fun removeLike(postId: String, userId: String, callback: (Boolean, String) -> Unit) {
        val postRef = ref.child(postId)
        postRef.get().addOnSuccessListener { snapshot ->
            val post = snapshot.getValue(Post::class.java)
            if (post != null) {
                if (post.likedBy.contains(userId)) {
                    val updatedLikes = post.likes - 1
                    val updatedLikedBy = post.likedBy.filter { it != userId }
                    postRef.child("likes").setValue(updatedLikes)
                    postRef.child("likedBy").setValue(updatedLikedBy)
                    callback(true, "Like removed successfully")
                } else {
                    callback(false, "Post not liked by this user")
                }
            } else {
                callback(false, "Post not found")
            }
        }.addOnFailureListener { e ->
            callback(false, e.message ?: "Failed to remove like")
        }
    }

    override fun addComment(postId: String, comment: String, userId: String, callback: (Boolean, String) -> Unit) {
        val postRef = ref.child(postId)
        postRef.get().addOnSuccessListener { snapshot ->
            val post = snapshot.getValue(Post::class.java)
            if (post != null) {
                // You can extend this to store userId with the comment if you want
                val updatedComments = post.comments + comment
                val updatedCommentsCount = updatedComments.size
                postRef.child("comments").setValue(updatedComments)
                postRef.child("commentsCount").setValue(updatedCommentsCount)
                callback(true, "Comment added successfully")
            } else {
                callback(false, "Post not found")
            }
        }.addOnFailureListener { e ->
            callback(false, e.message ?: "Failed to add comment")
        }
    }

    fun countComments(postId: String, callback: (Int?, String?) -> Unit) {
        val postRef = ref.child(postId).child("commentsCount")
        postRef.get().addOnSuccessListener { snapshot ->
            val count = snapshot.getValue(Int::class.java)
            callback(count, null)
        }.addOnFailureListener { e ->
            callback(null, e.message ?: "Failed to count comments")
        }
    }
}