package com.example.bact.service

import retrofit2.http.GET
import retrofit2.http.POST

interface BACTService {

    @POST("/postOriginImage")
    suspend fun postOriginImage() {

    }

    @GET("/getProcessedImage")
    suspend fun getProcessedImage() {

    }

    @GET("/queryProgress")
    suspend fun queryProgress() {

    }
}