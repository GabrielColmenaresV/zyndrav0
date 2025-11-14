//Esto se cambio commit
package com.example.zyndrav0.model

data class Message(
    val text: String,
    val isUser: Boolean, // true si es del usuario, false si es de n8n
    val timestamp: Long = System.currentTimeMillis(),
    val attachmentUri: String? = null,
    val attachmentType: String? = null // "image", "file", "camera"
)