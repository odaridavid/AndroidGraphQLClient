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

import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

//TODO config tests to work
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
internal class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer().apply {
            start(8080)
            url("/")
        }
        scenario = launch(MainActivity::class.java)
    }

    @Test
    fun shouldLoadCharacters() {
        onData(instanceOf(Character::class.java)).check(matches(isDisplayed()))
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

}