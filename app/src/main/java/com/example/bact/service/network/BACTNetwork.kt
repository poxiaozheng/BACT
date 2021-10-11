package com.example.bact.service.network

object BACTNetwork {

    private val retrofitService: BACTService by lazy {
        ServiceCreator.create()
    }

    suspend fun postOriginImage(pictureArray: ByteArray, multiple: Int, noiseGrade: Int) =
        retrofitService.postOriginImage(pictureArray, multiple, noiseGrade)

    suspend fun queryProgress(imageId: String, receipt: String) =
        retrofitService.queryProgress(imageId, receipt)
}