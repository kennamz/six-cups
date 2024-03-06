/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package six.cups.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import six.cups.data.MainScreenRepository
import six.cups.data.DefaultMainScreenRepository
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsMainScreenRepository(
        mainScreenRepository: DefaultMainScreenRepository
    ): MainScreenRepository
}

class FakeMainScreenRepository @Inject constructor() : MainScreenRepository {
    override val mainScreens: Flow<List<String>> = flowOf(fakeMainScreens)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakeMainScreens = listOf("One", "Two", "Three")
