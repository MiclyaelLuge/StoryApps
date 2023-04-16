package com.example.storyappsubmission.data.remote.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyappsubmission.data.remote.response.*
import com.example.storyappsubmission.data.remote.retrofit.UserAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepo private constructor(
    private val apiService: UserAPIService
) {
    private val registerresult = MediatorLiveData<Result<List<RegisterResponse>>>()
    private val loginresult = MediatorLiveData<Result<List<LoginResult>>>()

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<List<RegisterResponse>>> {
        registerresult.value = Result.Loading
        val client = apiService.postRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    val listResponse = ArrayList<RegisterResponse>()
                    val arrayResponse = RegisterResponse(
                        registerResponse!!.error,
                        registerResponse.message
                    )
                    listResponse.add(arrayResponse)
                    registerresult.value = Result.Success(listResponse)
                }else{
                    registerresult.value=Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerresult.value = Result.Error(t.message.toString())
            }

        })
        return registerresult

    }


    fun loginUser(email: String, password: String): LiveData<Result<List<LoginResult>>> {
        loginresult.value = Result.Loading
        val client = apiService.postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginvalue = response.body()?.loginResult
                    val listResponse = ArrayList<LoginResult>()
                    val arrayResponse = LoginResult(
                        loginvalue!!.name,
                        loginvalue.userId,
                        loginvalue.token
                    )
                    listResponse.add(arrayResponse)
                    loginresult.value = Result.Success(listResponse)
                } else {
                    loginresult.value = Result.Error(response.message())

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginresult.value = Result.Error(t.message.toString())
            }

        })
        return loginresult
    }


    companion object {
        @Volatile
        private var instance: LoginRepo? = null
        fun getInstance(
            apiService: UserAPIService
        ): LoginRepo =
            instance ?: synchronized(this) {
                instance ?: LoginRepo(apiService)
            }.also { instance = it }
    }
}