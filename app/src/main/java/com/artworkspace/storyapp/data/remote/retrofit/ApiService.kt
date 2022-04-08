package com.artworkspace.storyapp.data.remote.retrofit

import com.artworkspace.storyapp.data.remote.response.LoginResponse
import com.artworkspace.storyapp.data.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    /**
     * Call the API that handle a login process
     *
     * @param email User's email
     * @param password User's password
     */
    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse


    /**
     * Call the API that handle a registration process
     *
     * @param name User's name
     * @param email User's email
     * @param password User's password
     */
    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse
}