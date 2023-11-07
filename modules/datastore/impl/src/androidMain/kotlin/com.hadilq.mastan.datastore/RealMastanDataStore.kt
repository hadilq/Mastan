/**
 * Copyright 2023 Hadi Lashkari Ghouchani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hadilq.mastan.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.hadilq.mastan.log.LogLogicIo
import com.hadilq.mastan.root.RootState
import com.hadilq.mastan.root.initRootState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class RealMastanDataStore(
    private val applicationContext: Context,
    private val logger: LogLogicIo,
) : MastanDataStore {

    private val Context.dataStore: DataStore<RootState> by dataStore(
        "root_state_preferences",
        RootStateSerializer
    )

    override suspend fun save(rootState: RootState) {
        logger.logDebug { "RealMastanDataStore save $rootState" }
        applicationContext.dataStore.updateData {
            it.copy(rootState.authState, rootState.navigationState, rootState.themeState)
        }
    }

    override suspend fun load(): Result<RootState> {
        val result = kotlin.runCatching { applicationContext.dataStore.data.first() }
        logger.logDebug { "RealMastanDataStore load $result" }
        return result
    }
}

object RootStateSerializer : Serializer<RootState> {

    override val defaultValue = initRootState

    override suspend fun readFrom(input: InputStream): RootState =
        withContext(Dispatchers.IO) {
            try {
                Json.decodeFromString(
                    RootState.serializer(), input.readBytes().decodeToString()
                )
            } catch (serialization: SerializationException) {
                CorruptionException("Unable to read RootState", serialization).printStackTrace()
                initRootState
            }
        }

    override suspend fun writeTo(t: RootState, output: OutputStream) =
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(RootState.serializer(), t)
                    .encodeToByteArray()
            )
        }
}
