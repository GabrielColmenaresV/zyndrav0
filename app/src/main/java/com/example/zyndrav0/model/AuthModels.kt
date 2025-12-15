package com.example.zyndrav0.model

import com.google.gson.annotations.SerializedName

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

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String?,
    @SerializedName("usuario")
    val user: UserDto?,
    val error: String?
)

data class UserDto(
    val id: Int,
    @SerializedName("nombre")
    val username: String,
    val email: String
)