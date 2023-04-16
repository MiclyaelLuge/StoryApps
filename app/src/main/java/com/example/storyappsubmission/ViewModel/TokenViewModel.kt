package com.example.storyappsubmission.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyappsubmission.utils.LoginPreferences
import kotlinx.coroutines.launch

class TokenViewModel(private val pref:LoginPreferences) : ViewModel() {

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
    fun saveToken(token:String){
        viewModelScope.launch{
            pref.saveToken(token)
        }
    }
    fun deleteToken(){
        viewModelScope.launch {
            pref.deleteToken()
        }
    }
}