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
package com.hadilq.mastan.ui.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicReference

interface BasePresenter

abstract class Presenter<Event, Model, Effect>(
    initialState: Model,
) : BasePresenter {
    var model: Model by mutableStateOf(initialState)

    private val onGoingEvent: AtomicReference<MutableSet<Int>> = AtomicReference(mutableSetOf())

    val events: MutableSharedFlow<Event> =
        MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val effects: MutableSharedFlow<Effect> = MutableSharedFlow(extraBufferCapacity = 1)

    fun handle(event: Event) = events.tryEmit(event)
    suspend fun start(scope: CoroutineScope = GlobalScope) {
        events.collect { event ->
            val eventHash = event.hashCode()
            if (onGoingEvent.get().contains(eventHash)) {
                return@collect
            }
            onGoingEvent.get().add(eventHash)
            eventHandler(event, scope = scope)
            onGoingEvent.get().remove(eventHash)
        }
    }

    abstract suspend fun eventHandler(event: Event, scope: CoroutineScope)


}