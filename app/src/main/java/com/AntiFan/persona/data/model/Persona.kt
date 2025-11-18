package com.AntiFan.persona.data.model // 请确保包名与你的项目一致

import kotlinx.serialization.Serializable

/**
 * 代表一个 Persona 的核心数据结构。
 * @Serializable 注解是为了让 Kotlinx Serialization 库能够自动处理这个对象的 JSON 转换。
 *
 * @property id 唯一标识符，用于区分不同的 Persona。
 * @property name Persona 的名字。
 * @property avatarUrl 头像图片的 URL。在初期，我们可以使用一些网络上的占位图片。
 * @property personality 性格描述，例如 "开朗、富有创造力、略带神秘"。
 * @property backstory Persona 的背景故事。
 * @property creatorId 创建这个 Persona 的用户 ID。在单机版阶段，可以是一个固定值。
 */
@Serializable
data class Persona(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val personality: String,
    val backstory: String,
    val creatorId: String = "local_user" // 为 creatorId 提供一个默认值，简化单机版的处理
)