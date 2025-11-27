package com.AntiFan.persona.data.network

import com.AntiFan.persona.data.network.model.ChatRequest
import com.AntiFan.persona.data.network.model.ChatResponse
import com.AntiFan.persona.data.network.model.ImageGenerationRequest
import com.AntiFan.persona.data.network.model.ImageGenerationResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VolcEngineApi {

    // 对话接口
    @POST("api/v3/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse

    // ✅ 新增：生图接口
    @POST("api/v3/images/generations")
    suspend fun generateImage(
        @Header("Authorization") authorization: String,
        @Body request: ImageGenerationRequest
    ): ImageGenerationResponse
}