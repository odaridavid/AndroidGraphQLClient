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


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow



internal class MainViewModel : ViewModel() {

    private var currentResult: Flow<PagingData<Character>>? = null

    @ExperimentalCoroutinesApi
    fun getCharacters(): Flow<PagingData<Character>> {

        val newResult =
            Pager(PagingConfig(20)) { CharactersPagingSource() }
                .flow
                .cachedIn(viewModelScope)
        currentResult = newResult

        return newResult
    }

}
