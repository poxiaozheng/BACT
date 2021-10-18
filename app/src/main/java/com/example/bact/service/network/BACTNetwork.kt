package com.example.bact.service.network

import com.example.bact.BACTApplication
import com.example.bact.model.response.PostOriginImageResponse
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object BACTNetwork {

    private val retrofitService: BACTService by lazy {
        ServiceCreator.create()
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient().newBuilder().build()
    }

    suspend fun postOriginImage(pictureArray: ByteArray, scale: Int, noiseGrade: Int) =
        retrofitService.postOriginImage(pictureArray, scale, noiseGrade)

    suspend fun queryProgress(imageId: String, receipt: String) =
        retrofitService.queryProgress(imageId, receipt)

    fun startOkhttpRequest(
        pictureArray: ByteArray,
        scale: Int,
        noiseGrade: Int
    ): PostOriginImageResponse {
        val mediaType = MediaType.parse("image/jpg")
        val requestBody = RequestBody.create(mediaType, pictureArray)
        val request = Request.Builder()
            .url(ServiceCreator.getServiceBaseUrl() + "/bact/postOriginImage?scale=$scale&noiseGrade=$noiseGrade")
            .method("POST", requestBody)
            .addHeader("Content-Type", "image/jpg")
            .build()

        val response = BACTNetwork.okHttpClient!!.newCall(request).execute()
        val responseData = response.body()?.string()
        val gson = Gson()
        return gson.fromJson(responseData, PostOriginImageResponse::class.java)
    }
}