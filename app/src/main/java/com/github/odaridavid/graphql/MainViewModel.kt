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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
        initLoading()
        viewModelScope.launch {
            Pager(PagingConfig(20)) { CharactersPagingSource() }
                .flow
                .cachedIn(viewModelScope)
                .catch { e -> initError("${e.message}") }
                .collect { value: PagingData<Character> ->
                    initSuccess(value)
                }
        }
    }


    private fun initLoading() {
        _state.value = State.Loading
    }

    private fun initSuccess(data: PagingData<Character>) {
        _state.value = State.Success(data)
    }

    private fun initError(message: String) {
        _state.value = State.Error(message)
    }

}

internal sealed class State {
    data class Error(val message: String) : State()
    data class Success(val results: PagingData<Character>) : State()
    object Loading : State()
}
