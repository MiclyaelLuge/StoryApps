package com.example.storyappsubmission.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.DInjection.Injection
import com.example.storyappsubmission.data.remote.result.StoryRepo

class StoryViewModelFactory(private val repo: StoryRepo):
    ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create (modelClass:Class<T>):T{
            if(modelClass.isAssignableFrom(StoryViewModel::class.java)){
                return StoryViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel cass : "+ modelClass.name)
        }
    companion object{
        @Volatile
        private var instance: StoryViewModelFactory?=null
        fun getInstance(context: Context): StoryViewModelFactory =
            instance ?: synchronized(this){
                instance ?: synchronized(this){
                    instance ?: StoryViewModelFactory(Injection.provideStoryRepository(context))
                }.also{ instance =it}
            }
    }
    }