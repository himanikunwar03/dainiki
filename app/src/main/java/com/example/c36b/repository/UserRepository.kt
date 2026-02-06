package com.example.c36b.repository

import com.example.c36b.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {


    //    {
//        "success" : false,
//      "message": "Email sent to ram@gmail,com",

    //    }


    //real time database ko function
    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)


    fun getCurrentUser(): FirebaseUser?

    //    {
//        "success" : true,
//      "message": "logout succesfull",

    //    }
    fun getUserFromDatabase(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun logout(callback: (Boolean, String) -> Unit)

    fun editProfile(
        userId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    )

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit)

}