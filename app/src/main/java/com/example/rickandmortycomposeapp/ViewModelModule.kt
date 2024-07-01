package com.example.rickandmortycomposeapp

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CharacterViewModel(get()) }
}