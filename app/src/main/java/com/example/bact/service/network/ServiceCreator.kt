package com.example.bact.service.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private const val DEV_URL = "http://192.168.88.194:8081"

    private const val BASE_URL = "http://101.34.82.8:8082"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getServiceBaseUrl(): String {
        return BASE_URL
    }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}