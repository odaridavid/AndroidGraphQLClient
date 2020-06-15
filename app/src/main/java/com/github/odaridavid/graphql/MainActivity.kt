/**
 * Copyright 2020 David Odari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.odaridavid.graphql

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.github.odaridavid.graphql.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

internal class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var charactersAdapter: CharactersAdapter
    private val mainViewModel: MainViewModel by viewModels()
    private var pagingJob: Job? = null

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        charactersAdapter = CharactersAdapter()
        observeLoadState()
        onRefresh()
        observeState()
        mainViewModel.getCharacters()
    }

    @ExperimentalCoroutinesApi
    private fun onRefresh() {
        binding.swiperefreshlayout.setOnRefreshListener {
            //TODO Handle refresh in a better way with caching in mind
            pagingJob?.cancel()
            mainViewModel.getCharacters()
        }
    }

    private fun observeLoadState() {
        launch {
            charactersAdapter.loadStateFlow.collectLatest { state ->
                when (val s = state.refresh) {
                    is LoadState.Error -> showError("${s.error.message}")
                    is LoadState.Loading -> showLoading()
                    is LoadState.NotLoading -> hideLoading()
                }
            }
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun observeState() {
        mainViewModel.state.observe(this) { state ->
            when (state) {
                is State.Success -> showSuccess(state.results)
                is State.Loading -> showLoading()
                is State.Error -> showError(state.message)
            }
        }
    }

    private fun showLoading() {
        binding.loadingCharactersProgressBar.show()
    }

    private fun hideLoading() {
        binding.loadingCharactersProgressBar.hide()
    }

    private fun showSuccess(characters: PagingData<Character>) {
        hideLoading()
        hideError()
        binding.charactersRecyclerView.show()
        if (binding.swiperefreshlayout.isRefreshing) {
            binding.swiperefreshlayout.isRefreshing = false
        }
        pagingJob = launch {
            charactersAdapter.submitData(characters)
        }
        binding.charactersRecyclerView.adapter = charactersAdapter

    }

    private fun hideError(){
        binding.errorContainer.hide()
    }

    private fun showError(message: String) {
        hideLoading()
        binding.charactersRecyclerView.hide()
        binding.errorContainer.show()
        Snackbar
            .make(binding.charactersRecyclerView, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(loadColor(R.color.colorError))
            .setTextColor(loadColor(R.color.colorOnError))
            .show()
    }

}

