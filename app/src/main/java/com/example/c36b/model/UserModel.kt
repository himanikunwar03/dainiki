package com.example.c36b.model

data class UserModel(
    var userId : String = "",
    var email : String = "",
    var name: String="",
    var password:String="",
    var bio: String ="",
    var profileImageUrl: String? = null,
    var postCount: Int = 0,
    var followersCount: Int = 0,
    var followingCount: Int = 0
)