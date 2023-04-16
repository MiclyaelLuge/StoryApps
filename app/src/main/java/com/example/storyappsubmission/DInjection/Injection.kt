package com.example.storyappsubmission.DInjection

import android.content.Context
import com.example.storyappsubmission.data.remote.retrofit.LoginConfig
import com.example.storyappsubmission.data.remote.result.LoginRepo
import com.example.storyappsubmission.data.remote.result.StoryRepo

object Injection {
    fun provideRepository(context: Context): LoginRepo {
        val apiService = LoginConfig.getLoginService()
        return LoginRepo.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepo {
        val apiService = LoginConfig.getLoginService()
        return StoryRepo.getInstance(apiService)
    }
}