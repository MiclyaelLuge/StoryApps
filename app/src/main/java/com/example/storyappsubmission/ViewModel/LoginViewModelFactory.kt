package com.example.storyappsubmission.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.DInjection.Injection
import com.example.storyappsubmission.data.remote.result.LoginRepo

class LoginViewModelFactory private constructor(private val loginRepo: LoginRepo) :
ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(loginRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class :"+modelClass.name)
    }
    companion object{
        @Volatile
        private var instance : LoginViewModelFactory?=null
        fun getInstance(context: Context) : LoginViewModelFactory =
            instance ?: synchronized(this){
                instance ?: LoginViewModelFactory(Injection.provideRepository(context))
            }.also{ instance =it}

    }
}