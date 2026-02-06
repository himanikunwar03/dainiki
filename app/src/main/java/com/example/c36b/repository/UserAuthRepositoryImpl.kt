package com.example.c36b.repository

import com.google.firebase.auth.FirebaseAuth

class UserAuthRepositoryImpl(val auth: FirebaseAuth) : UserAuthRepository {

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    callback(true, "Login successfully")
                } else {
                    callback(false, "${res.exception?.message}")
                }

            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { res ->
                if (res.isSuccessful) {
                    callback(
                        true, "Registration successfull",
                        "${auth.currentUser?.uid}"
                    )
                } else {
                    callback(
                        false,
                        "${res.exception?.message}", ""
                    )
                }

            }
    }

}