package com.example.storyappsubmission.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.utils.LoginPreferences

class TokenViewModelFactory(private val pref:LoginPreferences):
    ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create (modelClass:Class<T>):T{
            if (modelClass.isAssignableFrom(TokenViewModel::class.java)){
                return TokenViewModel(pref) as T
            }
            throw IllegalArgumentException("Unknown ViewModel cass : "+ modelClass.name)
        }
}