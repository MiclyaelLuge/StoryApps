package com.example.storyappsubmission.ViewModel

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission.data.remote.result.StoryRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepo: StoryRepo) : ViewModel() {
    fun getAllStory()=storyRepo.getStory()
    fun getDetailedStory(id:String)=storyRepo.getDetailedStory(id)
    fun uploadStory(photo:MultipartBody.Part,description: RequestBody)=storyRepo.uploadStory(photo,description)
}