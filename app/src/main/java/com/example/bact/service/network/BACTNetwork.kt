package com.example.bact.service.network

import com.example.bact.service.BACTService

object BACTNetwork {

    private val retrofitService: BACTService by lazy {
        ServiceCreator.create<BACTService>()
    }

    suspend fun postOriginImage() = retrofitService.postOriginImage()

    suspend fun getProcessedImage() = retrofitService.getProcessedImage()

    suspend fun queryProgress() = retrofitService.queryProgress()
}