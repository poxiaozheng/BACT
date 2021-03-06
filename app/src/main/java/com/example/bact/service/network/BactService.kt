package com.example.bact.service.network

import com.example.bact.model.response.PostOriginImageResponse
import com.example.bact.model.response.QueryProgressResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BACTService {

    @POST("/bact/postOriginImage")
    suspend fun postOriginImage(
        @Body pictureArray: ByteArray,
        @Query("scale") scale: Int,
        @Query("noiseGrade") noiseGrade: Int
    ): PostOriginImageResponse


    @GET("/bact/queryProgress")
    suspend fun queryProgress(
        @Query("imageId") imageId: String,
        @Query("receipt") receipt: String
    ): QueryProgressResponse

}