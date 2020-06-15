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

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


internal object NetworkUtils {

    const val BASE_URL = "https://rickandmortyapi.com/graphql/"

    fun buildApolloClient(baseUrl: String): ApolloClient {
        return ApolloClient.builder()
            .okHttpClient(provideOkhttpClient())
            .serverUrl(baseUrl)
            .build()
    }

    private fun provideOkhttpClient(): OkHttpClient {
        val okhttp =
            OkHttpClient().newBuilder()
                .callTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)

        if (BuildConfig.DEBUG)
            okhttp.addInterceptor(provideLoggingInterceptor())

        return okhttp.build()
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
