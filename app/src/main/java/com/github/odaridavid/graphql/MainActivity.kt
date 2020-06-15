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
import com.github.odaridavid.graphql.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var charactersAdapter: CharactersAdapter
    private val mainViewModel: MainViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeState()
        mainViewModel.getCharacters()
    }

    private fun observeState() {
        mainViewModel.state.observe(this) { state ->
            when (state) {
                is State.Success -> showSuccess(state.results)
                is State.Loading -> showLoading()
                is State.Failure -> showError(state.message)
            }
        }
    }

    private fun showLoading() {
        binding.loadingCharactersProgressBar.show()
    }

    private fun showSuccess(characters: List<Character>) {
        charactersAdapter = CharactersAdapter()
        charactersAdapter.submitList(characters)
        binding.charactersRecyclerView.adapter = charactersAdapter
        binding.loadingCharactersProgressBar.hide()
    }

    private fun showError(message: String) {
        binding.loadingCharactersProgressBar.hide()
        binding.errorContainer.show()
        Snackbar
            .make(binding.charactersRecyclerView, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(loadColor(R.color.colorError))
            .setTextColor(loadColor(R.color.colorOnError))
            .show()
    }

}

