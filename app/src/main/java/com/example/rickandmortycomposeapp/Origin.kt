package com.example.rickandmortycomposeapp

import kotlinx.serialization.Serializable

@Serializable
data class Origin(
    val name: String,
    val url: String
)