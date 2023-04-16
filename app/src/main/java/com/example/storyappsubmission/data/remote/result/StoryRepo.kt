package com.example.storyappsubmission.data.remote.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyappsubmission.data.remote.response.*
import com.example.storyappsubmission.data.remote.retrofit.UserAPIService
import com.example.storyappsubmission.ui.LoginFragment
import com.example.storyappsubmission.utils.LoginPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepo private constructor(
    private val apiService: UserAPIService,
    private val token: String = LoginFragment.Token
) {
    private val liststoryresult = MediatorLiveData<Result<List<ListStoryItem>>>()
    private val detailstoryresult = MediatorLiveData<Result<DetailStoryResponse>>()
    private val uploadstoryresult = MediatorLiveData<Result<AddStoryResponse>>()

    fun getDetailedStory(id: String): LiveData<Result<DetailStoryResponse>> {
        detailstoryresult.value = Result.Loading
        val client = apiService.getDetailStories("Bearer $token", id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                if (response.isSuccessful) {
                    detailstoryresult.value = Result.Success(response.body()!!)
                } else {
                    detailstoryresult.value = Result.Error(response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                detailstoryresult.value = Result.Error(t.message.toString())
            }

        })
        return detailstoryresult
    }

    fun getStory(): LiveData<Result<List<ListStoryItem>>> {
        liststoryresult.value = Result.Loading
        val client = apiService.getAllStories("Bearer $token")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    liststoryresult.value = Result.Success(response.body()!!.listStory)

                } else {
                    liststoryresult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                liststoryresult.value = Result.Error(t.message.toString())
            }

        })
        return liststoryresult
    }

    fun uploadStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> {
        uploadstoryresult.value = Result.Loading
        val service = apiService.addStories("Bearer $token", imageMultipart, description)
        service.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    uploadstoryresult.value = Result.Success(response.body()!!)

                } else {
                    uploadstoryresult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                uploadstoryresult.value = Result.Error(t.message.toString())
            }

        })
        return uploadstoryresult

    }

    companion object {
        @Volatile
        private var instance: StoryRepo? = null
        fun getInstance(
            apiService: UserAPIService
        ): StoryRepo =
            instance ?: synchronized(this) {
                instance ?: StoryRepo(apiService)
            }.also { instance = it }
    }

}