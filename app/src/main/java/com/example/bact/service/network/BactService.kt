package com.example.bact.service.network

import com.example.bact.model.response.PostOriginImageResponse
import com.example.bact.model.response.QueryProgressResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface BACTService {

    @POST("/bact/postOriginImage")
    suspend fun postOriginImage(
        @Body pictureArray: ByteArray,
        @Query("scale") scale: Int,
        @Query("noiseGrade") noiseGrade: Int
    ): PostOriginImageResponse

//    @Multipart
//    @POST("/bact/postOriginImage")
//    suspend fun postOriginImage(
//        @Part pictureArray: MultipartBody.Part,
//        @Query("scale") scale: Int,
//        @Query("noiseGrade") noiseGrade: Int
//    ): PostOriginImageResponse


    @GET("/bact/queryProgress")
    suspend fun queryProgress(
        @Query("imageId") imageId: String,
        @Query("receipt") receipt: String
    ): QueryProgressResponse

}