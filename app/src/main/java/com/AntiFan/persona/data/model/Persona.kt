package com.AntiFan.persona.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "personas")
@Serializable
data class Persona(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val personality: String,
    val backstory: String,
    val creatorId: String = "local_user"
)