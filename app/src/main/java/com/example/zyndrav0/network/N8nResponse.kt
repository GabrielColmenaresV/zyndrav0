package com.example.zyndrav0.network

import com.google.gson.annotations.SerializedName

data class N8nResponse(
    val status: String,

    @SerializedName("reply_text")
    val replyText: String
)
