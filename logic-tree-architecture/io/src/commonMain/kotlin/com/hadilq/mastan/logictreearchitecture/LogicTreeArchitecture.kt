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
package com.hadilq.mastan.logictreearchitecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope

val LocalLogicTreeArchitecture =
    compositionLocalOf<LogicTreeArchitecture> { error("No component found!") }

interface LogicTreeArchitecture {

    /**
     * Aggregate the [Throwable]s in the [Result], and also switch to IO executor.
     */
    suspend fun <T> runCatchingOnIO(
        block: suspend CoroutineScope.() -> T,
    ): Result<T>

    /**
     * Switch to IO executor.
     */
    suspend fun <T> runOnIO(
        block: suspend CoroutineScope.() -> T,
    ): T

    /**
     * Return a single instance and cache it for future calls. In case you are sure you need
     * the instance in one thread only.
     */
    fun <T> singleWithNoRace(initializer: () -> T): Lazy<T>

    /**
     * Return a single instance and cache it for future calls. In case you may need the
     * instance in different threads, so you may have race conditions.
     */
    fun <T> single(initializer: () -> T): Lazy<T>

    /**
     * Provide an implementation of [LogicWithEventSink].
     */
    fun <S, E> logicWithEventSink(defaultState: S, noEvent: E): LogicWithEventSink<S, E>
}

/**
 * Compose can handle the scoping of instances. Here we renamed [remember] to [singleInThisScope]
 * to make it clear what's the purpose.
 */
@Composable
inline fun <T> singleInThisScope(crossinline calculation: @DisallowComposableCalls () -> T): T =
    remember(calculation)
