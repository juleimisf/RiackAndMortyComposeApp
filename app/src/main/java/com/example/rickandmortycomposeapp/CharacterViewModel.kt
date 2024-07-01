package com.example.rickandmortycomposeapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterViewModel(private val remoteRepositoryImpl: RemoteRepositoryImpl) : ViewModel() {
    private val _items: MutableStateFlow<List<Character>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<Character>> = _items

    suspend fun loadItems() {
        viewModelScope.launch {
            remoteRepositoryImpl.getCharacter().onSuccess { quotesResult ->
                _items.update {
                    quotesResult.results
                }
            }.onFailure {
                // something went wrong
            }
        }
    }
}