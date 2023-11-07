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
package com.hadilq.mastan.auth

import androidx.compose.runtime.compositionLocalOf
import com.hadilq.mastan.logictreearchitecture.di.LogicIoDefinition
import kotlinx.coroutines.Dispatchers

val LocalAuthLogicIo = compositionLocalOf<AuthLogicIo> { error("No component found!") }

/**
 * This is the definition of `auth` module. Its implementation is available in the `:auth:impl` module.
 * This indicates the functionality that `auth` module provides for other modules.
 */
@LogicIoDefinition
interface AuthLogicIo {
    val authLogic: AuthLogic
    val state: AuthState

    /**
     * Call it with [Dispatchers.Main].
     */
    val eventSink: suspend (AuthEvent) -> Unit

    fun authorizationHeader(accessTokenRequest: AccessTokenRequest): String
}
