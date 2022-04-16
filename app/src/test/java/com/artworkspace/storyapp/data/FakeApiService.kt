package com.artworkspace.storyapp.data

import com.artworkspace.storyapp.data.remote.response.FileUploadResponse
import com.artworkspace.storyapp.data.remote.response.LoginResponse
import com.artworkspace.storyapp.data.remote.response.RegisterResponse
import com.artworkspace.storyapp.data.remote.response.StoriesResponse
import com.artworkspace.storyapp.data.remote.retrofit.ApiService
import com.artworkspace.storyapp.utils.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyFileUploadResponse = DataDummy.generateDummyFileUploadResponse()

    override suspend fun userLogin(email: String, password: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun getAllStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): StoriesResponse {
        return dummyStoriesResponse
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): FileUploadResponse {
        return dummyFileUploadResponse
    }
}