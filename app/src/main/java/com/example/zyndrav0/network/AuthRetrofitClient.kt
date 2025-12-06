package com.example.zyndrav0.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AuthRetrofitClient {

    // ESTA ES LA NUEVA URL BASE
    private const val BASE_URL = "http://3.208.218.72:3000/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Creamos la instancia pública para llamar a la API
    val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Usamos Gson para JSON
            .build()
            .create(AuthApiService::class.java)
    }
}