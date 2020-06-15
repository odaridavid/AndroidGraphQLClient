/**
 *
 * Copyright 2020 David Odari
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *            http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 **/
package com.github.odaridavid.graphql

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


internal class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    @ExperimentalCoroutinesApi
    fun getCharacters() {
        _state.value = State.Loading
        viewModelScope.launch {
            CharacterRepository.fetchAllCharacters()
                .catch { e ->
                    _state.value = State.Failure("${e.message}")
                }
                .collect { response: Response<AllCharactersQuery.Data?> ->
                    response.data?.characters?.results?.let { results ->
                        val characters = mapResponseToPresentationModel(results)
                        _state.value = State.Success(characters)
                    }
                }
        }
    }

    private fun mapResponseToPresentationModel(results: List<AllCharactersQuery.Result?>): List<Character> {
        val characters = mutableListOf<Character>()
        for (result in results) {
            val characterImage = result?.image
            val characterName = result?.name
            characters.add(Character(characterName, characterImage))
        }
        return characters
    }
}

internal sealed class State {
    data class Failure(val message: String) : State()
    data class Success(val results: List<Character>) : State()
    object Loading : State()
}
