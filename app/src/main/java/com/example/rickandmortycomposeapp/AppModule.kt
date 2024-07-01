package com.example.rickandmortycomposeapp

import org.koin.dsl.module

val appModule = module {
    includes(viewModelModule, networkModule, repositoryModule)
}