package com.example.rickandmortycomposeapp

import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single { provideApiService(get()) }
    single { provideHttpClient() }
}

fun provideHttpClient(): HttpClient {
    return charactersClient
}

fun provideApiService(httpClient: HttpClient): ApiService {
    return ApiService(httpClient)
}