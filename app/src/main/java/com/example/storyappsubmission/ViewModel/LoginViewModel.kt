package com.example.storyappsubmission.ViewModel

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission.data.remote.result.LoginRepo

class LoginViewModel(private val loginRepo: LoginRepo) : ViewModel() {
    fun postLoginUser(email:String,password:String)=loginRepo.loginUser(email,password)
    fun postRegisterUser(name:String,email:String,password:String)=loginRepo.registerUser(name,email,password)
}