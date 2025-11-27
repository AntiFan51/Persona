package com.AntiFan.persona.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageGenerationRequest(
    val model: String,
    val prompt: String,
    val width: Int = 512,
    val height: Int = 512
)

@Serializable
data class ImageGenerationResponse(
    val data: List<ImageData>,
    val created: Long
)

@Serializable
data class ImageData(
    val url: String
)