package com.example.myapplication.data.remote.service

import com.example.myapplication.data.remote.model.LoginRequestBody
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AppApiService {

    @POST("login")
    suspend fun login(@Body body: LoginRequestBody)// :Response<ApiResponseRemote<LoginResponse>>
}