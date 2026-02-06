package com.example.c36b.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.c36b.model.UserModel
import com.example.c36b.repository.UserAuthRepository
import com.example.c36b.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo : UserRepository,val authRepo : UserAuthRepository) : ViewModel(){


    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    ){
        authRepo.login(email,password,callback)
    }

    //authentication ko function
    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    ){
        authRepo.register(email,password,callback)
    }

    //real time database ko function
    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repo.addUserToDatabase(userId,model,callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit){
        repo.forgetPassword(email,callback)
    }


    fun getCurrentUser(): FirebaseUser?{
        return repo.getCurrentUser()
    }



    private val _users = MutableLiveData<UserModel?>()
    val users : LiveData<UserModel?> get() = _users

    fun getUserFromDatabase(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ){
     repo.getUserFromDatabase(userId){
         success,message,users->
         if(success){
             _users.postValue(users)
         }else{
             _users.postValue(null)
         }
     }
    }

    fun logout(callback: (Boolean, String) -> Unit){
        repo.logout(callback)
    }

    fun editProfile(
        userId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ){
        repo.editProfile(userId,data,callback)
    }

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit){
        repo.deleteAccount(userId,callback)
    }

}