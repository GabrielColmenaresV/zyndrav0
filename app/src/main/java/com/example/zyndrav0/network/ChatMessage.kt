package com.example.zyndrav0.network

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("sender")
    val sender: String, // Nombre o ID del usuario

    @SerializedName("message")
    val message: String, // El contenido del mensaje

    @SerializedName("timestamp")
    val timestamp: Long // Marca de tiempo para referencia
)
