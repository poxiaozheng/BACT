package com.example.bact.service.network

import okhttp3.MultipartBody
import java.io.File

object BACTNetwork {

    private val retrofitService: BACTService by lazy {
        ServiceCreator.create()
    }

    suspend fun postOriginImage(pictureArray: ByteArray, scale: Int, noiseGrade: Int) =
        retrofitService.postOriginImage(pictureArray, scale, noiseGrade)

    suspend fun queryProgress(imageId: String, receipt: String) =
        retrofitService.queryProgress(imageId, receipt)
}