package com.artworkspace.storyapp.data.remote.retrofit

import com.artworkspace.storyapp.data.remote.response.FileUploadResponse
import com.artworkspace.storyapp.data.remote.response.LoginResponse
import com.artworkspace.storyapp.data.remote.response.RegisterResponse
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

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
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse


    /**
     * Call the API to provide all stories data
     *
     * @param token User's authentication token
     * @param page
     * @param size
     */
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): StoriesResponse

    /**
     * Call the API that handle uploading image process
     *
     * @param token User's authentication token
     * @param file Multipart image
     * @param description Image description
     */
    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): FileUploadResponse
}