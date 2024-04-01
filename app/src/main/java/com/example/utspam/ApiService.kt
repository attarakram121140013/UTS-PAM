package com.example.utspam

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users")
    fun getUsers(): Call<UserResponse>
    abstract fun getUser(@Path("userId") userId: Int): Call<UserResponse>
}

