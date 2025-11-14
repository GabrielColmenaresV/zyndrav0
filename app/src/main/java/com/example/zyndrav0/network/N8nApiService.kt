package com.example.zyndrav0.network

import retrofit2.http.Body
import retrofit2.http.POST

interface N8nApiService {

    @POST("webhook/zydra")
    suspend fun sendMessage(@Body message: ChatMessage): String // La respuesta es texto plano, no un objeto JSON debido a que hubieron multiples errores.
}
