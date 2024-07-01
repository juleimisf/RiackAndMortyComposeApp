package com.example.rickandmortycomposeapp

import io.ktor.client.call.body

class RemoteRepositoryImpl(
    private val apiService: ApiService
) : RemoteRepository {

    override suspend fun getCharacter(): Result<CharactersItem> {
        return try {
            Result.success(apiService.getCharacters().body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}