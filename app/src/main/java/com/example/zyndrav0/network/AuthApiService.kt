package com.example.zyndrav0.network

import com.example.zyndrav0.model.RegisterRequest
import com.example.zyndrav0.model.RegisterResponse
import com.example.zyndrav0.model.LoginRequest   // Asegúrate de importar esto
import com.example.zyndrav0.model.LoginResponse  // Y esto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    // Aquí usamos la ruta relativa, ya que la base la pondremos en el cliente
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    // 👇 AGREGA ESTE PARA EL LOGIN
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}
