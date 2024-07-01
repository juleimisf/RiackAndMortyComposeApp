package com.example.rickandmortycomposeapp

import org.koin.dsl.module

val repositoryModule = module {
    single { provideRemoteRepository(get()) }
}

fun provideRemoteRepository(apiService: ApiService): RemoteRepositoryImpl {
    return RemoteRepositoryImpl(apiService)
}