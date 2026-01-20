package models

import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    val name: String,
    val type: String,
    val powerLevel: Int
)