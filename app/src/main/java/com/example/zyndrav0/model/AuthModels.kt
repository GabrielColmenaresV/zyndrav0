package com.example.zyndrav0.model

import com.google.gson.annotations.SerializedName

// --- REGISTRO (Lo que ya tenías) ---
data class RegisterRequest(
    @SerializedName("nombre")
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val message: String?,
    val token: String?,
    val userId: Int?,
    val user: UserDto?
)

// --- LOGIN (LO NUEVO) ---
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String?,
    // El servidor devuelve un objeto llamado "usuario"
    @SerializedName("usuario")
    val user: UserDto?,
    val error: String?
)

// --- USUARIO COMÚN ---
data class UserDto(
    val id: Int,
    // 👇 OJO AQUÍ: Agregamos esto para que cuando el servidor responda
    // {"nombre": "Admin"}, tu app sepa guardarlo en la variable 'username'
    @SerializedName("nombre")
    val username: String,
    val email: String
)