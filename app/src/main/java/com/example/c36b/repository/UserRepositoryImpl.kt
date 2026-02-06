package com.example.c36b.repository

import com.example.c36b.model.UserModel
import com.example.c36b.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserRepositoryImpl : UserRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    val ref: DatabaseReference = database.reference.child("users")


    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User successfully added")
            } else {
                callback(true, "${it.exception?.message}")

            }
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    callback(true, "Reset email sent to $email")
                } else {
                    callback(false, "${res.exception?.message}")
                }

            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successfully")
        } catch (e: Exception) {
            callback(true, "${e.message}")
        }
    }

    override fun getUserFromDatabase(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var users = snapshot.getValue(UserModel::class.java)
                        if (users != null) {
                            callback(true, "User fetched", users)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }


    override fun editProfile(
        userId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated ")
            } else {
                callback(true, "${it.exception?.message}")

            }
        }
    }

    override fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "No user is currently signed in")
            return
        }

        // First, get user data to find username for cleaning up posts
        ref.child(userId).get().addOnSuccessListener { snapshot ->
            val userModel = snapshot.getValue(UserModel::class.java)
            val username = userModel?.name ?: ""

            // Clean up user's posts and likes
            cleanupUserData(userId, username) { cleanupSuccess ->
                if (cleanupSuccess) {
                    // Delete user data from Realtime Database
                    ref.child(userId).removeValue().addOnCompleteListener { databaseTask ->
                        if (databaseTask.isSuccessful) {
                            // Then delete the user from Firebase Authentication
                            currentUser.delete().addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    callback(true, "Account deleted successfully from both database and authentication")
                                } else {
                                    // If auth deletion fails, we should still report the database deletion
                                    callback(false, "Database deleted but authentication deletion failed: ${authTask.exception?.message}")
                                }
                            }
                        } else {
                            callback(false, "Failed to delete from database: ${databaseTask.exception?.message}")
                        }
                    }
                } else {
                    callback(false, "Failed to cleanup user data")
                }
            }
        }.addOnFailureListener { e ->
            callback(false, "Failed to get user data: ${e.message}")
        }
    }

    private fun cleanupUserData(userId: String, username: String, callback: (Boolean) -> Unit) {
        val postsRef = database.reference.child("posts")
        
        // Remove all posts by this user
        postsRef.orderByChild("username").equalTo(username).get().addOnSuccessListener { snapshot ->
            val deleteTasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()
            
            for (postSnapshot in snapshot.children) {
                deleteTasks.add(postSnapshot.ref.removeValue())
            }
            
            // Remove user from likedBy lists in all posts
            postsRef.get().addOnSuccessListener { allPostsSnapshot ->
                val updateTasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()
                
                for (postSnapshot in allPostsSnapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null && post.likedBy.contains(userId)) {
                        val updatedLikedBy = post.likedBy.filter { it != userId }
                        val updatedLikes = post.likes - 1
                        updateTasks.add(postSnapshot.ref.child("likedBy").setValue(updatedLikedBy))
                        updateTasks.add(postSnapshot.ref.child("likes").setValue(updatedLikes))
                    }
                }
                
                // Execute all cleanup tasks
                com.google.android.gms.tasks.Tasks.whenAll(deleteTasks + updateTasks)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }.addOnFailureListener {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

}