package com.AntiFan.persona.data.network

import com.AntiFan.persona.data.network.model.ChatRequest
import com.AntiFan.persona.data.network.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// 确保这里是 interface，而不是 class
interface VolcEngineApi {

    @POST("api/v3/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse
}