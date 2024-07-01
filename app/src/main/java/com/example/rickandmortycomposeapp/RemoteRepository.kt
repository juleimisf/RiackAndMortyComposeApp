package com.example.rickandmortycomposeapp

interface RemoteRepository {
    suspend fun getCharacter(): Result<CharactersItem>
}