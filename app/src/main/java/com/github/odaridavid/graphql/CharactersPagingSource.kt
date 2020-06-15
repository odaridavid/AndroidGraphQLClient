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

import android.util.Log
import androidx.paging.PagingSource
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.IOException
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
internal class CharactersPagingSource : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = CharacterRepository.fetchAllCharacters(nextPageNumber)
            val prevKey = response.data?.characters?.info?.prev
            val nextKey = response.data?.characters?.info?.next
            val data = response.data?.characters?.results ?: emptyList()
            val characters = mapResponseToPresentationModel(data)
            if (!response.hasErrors())
                LoadResult.Page(data = characters, nextKey = nextKey, prevKey = prevKey)
            else
                LoadResult.Error(Exception(response.errors?.first()?.message))
        } catch (e: IOException) {
            Log.e("Paging Source", "IOException :${e.message}")
            LoadResult.Error(e)
        } catch (e: UnknownHostException) {
            Log.e("Paging Source", "UnknownHost Exception :${e.message}")
            LoadResult.Error(e)
        } catch (e: ApolloNetworkException) {
            Log.e("Paging Source", " Apollo Network Exception : ${e.message}")
            LoadResult.Error(e)
        } catch (e: ApolloHttpException) {
            Log.e("Paging Source", "Apollo Http Exception ${e.message}")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("Paging Source", "Apollo Http Exception ${e.message}")
            LoadResult.Error(e)
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