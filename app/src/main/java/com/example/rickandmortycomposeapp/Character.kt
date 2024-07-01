package com.example.rickandmortycomposeapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    @SerialName("image") val imageUrl: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)