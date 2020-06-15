/**
 *
 * Copyright 2020 David Odari
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 **/
package com.github.odaridavid.graphql;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.github.odaridavid.graphql.databinding.ItemCharacterBinding


internal class CharactersAdapter :
    PagingDataAdapter<Character, CharactersAdapter.CharacterViewHolder>(DiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemCharacterBinding.inflate(inflater, parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int): Unit =
        getItem(position).let { holder.bind(it) }

    inner class CharacterViewHolder(
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character?) {
            binding.characterNameTextView.text = character?.name
            binding.characterImageView.load(character?.imageUrl) {
                placeholder(getRandomColor())
            }
        }
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
                return oldItem.imageUrl == newItem.imageUrl && oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
                return oldItem == newItem
            }
        }

        @ColorRes
        fun getRandomColor(): Int {
            return when ((1..3).random()) {
                1 -> R.color.colorAccent
                2 -> R.color.colorPrimary
                else -> R.color.colorPrimaryDark
            }
        }
    }
}