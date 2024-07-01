package com.example.rickandmortycomposeapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharactersItem(
    @SerialName("results")
    val results: List<Character>
)