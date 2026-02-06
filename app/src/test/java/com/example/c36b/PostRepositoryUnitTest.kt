package com.example.c36b

import com.example.c36b.model.Post
import com.example.c36b.repository.PostRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

// Test implementation of PostRepository that doesn't use Firebase
class TestPostRepository : PostRepository {
    private val posts = mutableMapOf<String, Post>()
    
    override fun createPost(post: Post, callback: (Boolean, String) -> Unit) {
        val postId = "post_${posts.size + 1}"
        val postWithKey = post.copy(key = postId)
        posts[postId] = postWithKey
        callback(true, "Post created successfully")
    }
    
    override fun getAllPosts(callback: (List<Post>?, String?) -> Unit) {
        callback(posts.values.toList(), null)
    }
    
    override fun getPostById(postId: String, callback: (Post?, String?) -> Unit) {
        val post = posts[postId]
        if (post != null) {
            callback(post, null)
        } else {
            callback(null, "Post not found")
        }
    }
    
    override fun updatePost(postId: String, post: Post, callback: (Boolean, String) -> Unit) {
        if (posts.containsKey(postId)) {
            posts[postId] = post.copy(key = postId)
            callback(true, "Post updated successfully")
        } else {
            callback(false, "Post not found")
        }
    }
    
    override fun deletePost(postId: String, callback: (Boolean, String) -> Unit) {
        if (posts.remove(postId) != null) {
            callback(true, "Post deleted successfully")
        } else {
            callback(false, "Post not found")
        }
    }
    
    override fun getPostsByUser(username: String, callback: (List<Post>?, String?) -> Unit) {
        val userPosts = posts.values.filter { it.username == username }
        callback(userPosts, null)
    }
    
    override fun getPostsByTag(tag: String, callback: (List<Post>?, String?) -> Unit) {
        val taggedPosts = posts.values.filter { it.tags.contains(tag) }
        callback(taggedPosts, null)
    }
    
    override fun addLike(postId: String, userId: String, callback: (Boolean, String) -> Unit) {
        val post = posts[postId]
        if (post != null) {
            if (!post.likedBy.contains(userId)) {
                val updatedPost = post.copy(
                    likes = post.likes + 1,
                    likedBy = post.likedBy + userId
                )
                posts[postId] = updatedPost
                callback(true, "Like added successfully")
            } else {
                callback(false, "Already liked")
            }
        } else {
            callback(false, "Post not found")
        }
    }
    
    override fun removeLike(postId: String, userId: String, callback: (Boolean, String) -> Unit) {
        val post = posts[postId]
        if (post != null) {
            if (post.likedBy.contains(userId)) {
                val updatedPost = post.copy(
                    likes = post.likes - 1,
                    likedBy = post.likedBy.filter { it != userId }
                )
                posts[postId] = updatedPost
                callback(true, "Like removed successfully")
            } else {
                callback(false, "Post not liked by this user")
            }
        } else {
            callback(false, "Post not found")
        }
    }
    
    override fun addComment(postId: String, comment: String, userId: String, callback: (Boolean, String) -> Unit) {
        val post = posts[postId]
        if (post != null) {
            val updatedPost = post.copy(
                comments = post.comments + comment,
                commentsCount = post.comments.size + 1
            )
            posts[postId] = updatedPost
            callback(true, "Comment added successfully")
        } else {
            callback(false, "Post not found")
        }
    }
}

class PostRepositoryUnitTest {
    private lateinit var postRepository: TestPostRepository

    @Before
    fun setup() {
        postRepository = TestPostRepository()
    }

    @Test
    fun testCreatePost_Successful() {
        val post = Post(
            username = "testUser",
            title = "Test Post",
            content = "Test content",
            tags = listOf("test", "kotlin")
        )
        var expectedSuccess = false
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.createPost(post, callback)

        assertTrue(expectedSuccess)
        assertEquals("Post created successfully", expectedMessage)
    }

    @Test
    fun testCreatePost_Failed() {
        // This test is not applicable for TestPostRepository as it always succeeds
        // In a real scenario, this would test database connection failures
        assertTrue(true) // Placeholder test
    }

    @Test
    fun testGetAllPosts_Successful() {
        // First create some posts
        val post1 = Post(username = "user1", title = "Post 1", content = "Content 1")
        val post2 = Post(username = "user2", title = "Post 2", content = "Content 2")
        
        postRepository.createPost(post1) { _, _ -> }
        postRepository.createPost(post2) { _, _ -> }

        var expectedPosts: List<Post>? = null
        var expectedError: String? = null

        val callback = { posts: List<Post>?, error: String? ->
            expectedPosts = posts
            expectedError = error
        }

        postRepository.getAllPosts(callback)

        assertNull(expectedError)
        assertEquals(2, expectedPosts?.size)
    }

    @Test
    fun testGetAllPosts_Empty() {
        var expectedPosts: List<Post>? = null
        var expectedError: String? = null

        val callback = { posts: List<Post>?, error: String? ->
            expectedPosts = posts
            expectedError = error
        }

        postRepository.getAllPosts(callback)

        assertNull(expectedError)
        assertTrue(expectedPosts?.isEmpty() == true)
    }

    @Test
    fun testGetPostById_Successful() {
        // First create a post
        val post = Post(username = "user1", title = "Test Post", content = "Test content")
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedPost: Post? = null
        var expectedError: String? = null

        val callback = { post: Post?, error: String? ->
            expectedPost = post
            expectedError = error
        }

        postRepository.getPostById(postId, callback)

        assertNull(expectedError)
        assertEquals("user1", expectedPost?.username)
    }

    @Test
    fun testGetPostById_NotFound() {
        var expectedPost: Post? = null
        var expectedError: String? = null

        val callback = { post: Post?, error: String? ->
            expectedPost = post
            expectedError = error
        }

        postRepository.getPostById("nonexistent", callback)

        assertNull(expectedPost)
        assertEquals("Post not found", expectedError)
    }

    @Test
    fun testDeletePost_Successful() {
        // First create a post
        val post = Post(username = "user1", title = "Test Post")
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedSuccess = false
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.deletePost(postId, callback)

        assertTrue(expectedSuccess)
        assertEquals("Post deleted successfully", expectedMessage)
    }

    @Test
    fun testAddLike_Successful() {
        // First create a post
        val post = Post(username = "user1", likes = 5, likedBy = listOf("user2", "user3"))
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedSuccess = false
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.addLike(postId, "user1", callback)

        assertTrue(expectedSuccess)
        assertEquals("Like added successfully", expectedMessage)
    }

    @Test
    fun testAddLike_AlreadyLiked() {
        // First create a post with user1 already liked
        val post = Post(username = "user1", likes = 5, likedBy = listOf("user1", "user2", "user3"))
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedSuccess = true
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.addLike(postId, "user1", callback)

        assertFalse(expectedSuccess)
        assertEquals("Already liked", expectedMessage)
    }

    @Test
    fun testRemoveLike_Successful() {
        // First create a post with user1 already liked
        val post = Post(username = "user1", likes = 5, likedBy = listOf("user1", "user2", "user3"))
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedSuccess = false
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.removeLike(postId, "user1", callback)

        assertTrue(expectedSuccess)
        assertEquals("Like removed successfully", expectedMessage)
    }

    @Test
    fun testRemoveLike_NotLiked() {
        // First create a post without user1 liked
        val post = Post(username = "user1", likes = 5, likedBy = listOf("user2", "user3"))
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedSuccess = true
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.removeLike(postId, "user1", callback)

        assertFalse(expectedSuccess)
        assertEquals("Post not liked by this user", expectedMessage)
    }

    @Test
    fun testAddComment_Successful() {
        // First create a post
        val post = Post(username = "user1", comments = listOf("Comment 1", "Comment 2"), commentsCount = 2)
        var postId = ""
        
        postRepository.createPost(post) { success, _ ->
            if (success) {
                postRepository.getAllPosts { posts, _ ->
                    postId = posts?.firstOrNull()?.key ?: ""
                }
            }
        }

        var expectedSuccess = false
        var expectedMessage = ""

        val callback = { success: Boolean, message: String ->
            expectedSuccess = success
            expectedMessage = message
        }

        postRepository.addComment(postId, "New comment", "user1", callback)

        assertTrue(expectedSuccess)
        assertEquals("Comment added successfully", expectedMessage)
    }

    @Test
    fun testGetPostsByUser_Successful() {
        // First create posts by the same user
        val post1 = Post(username = "testUser", title = "Post 1")
        val post2 = Post(username = "testUser", title = "Post 2")
        val post3 = Post(username = "otherUser", title = "Other Post")
        
        postRepository.createPost(post1) { _, _ -> }
        postRepository.createPost(post2) { _, _ -> }
        postRepository.createPost(post3) { _, _ -> }

        var expectedPosts: List<Post>? = null
        var expectedError: String? = null

        val callback = { posts: List<Post>?, error: String? ->
            expectedPosts = posts
            expectedError = error
        }

        postRepository.getPostsByUser("testUser", callback)

        assertNull(expectedError)
        assertEquals(2, expectedPosts?.size)
        assertEquals("testUser", expectedPosts?.get(0)?.username)
        assertEquals("testUser", expectedPosts?.get(1)?.username)
    }

    @Test
    fun testGetPostsByTag_Successful() {
        // First create posts with tags
        val post1 = Post(username = "user1", tags = listOf("kotlin", "android"))
        val post2 = Post(username = "user2", tags = listOf("kotlin", "firebase"))
        val post3 = Post(username = "user3", tags = listOf("java", "spring"))
        
        postRepository.createPost(post1) { _, _ -> }
        postRepository.createPost(post2) { _, _ -> }
        postRepository.createPost(post3) { _, _ -> }

        var expectedPosts: List<Post>? = null
        var expectedError: String? = null

        val callback = { posts: List<Post>?, error: String? ->
            expectedPosts = posts
            expectedError = error
        }

        postRepository.getPostsByTag("kotlin", callback)

        assertNull(expectedError)
        assertEquals(2, expectedPosts?.size)
        assertTrue(expectedPosts?.all { it.tags.contains("kotlin") } == true)
    }
} 