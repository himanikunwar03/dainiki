package com.example.c36b.repository

interface UserAuthRepository {
    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    )

    //authentication ko function
    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    )
}