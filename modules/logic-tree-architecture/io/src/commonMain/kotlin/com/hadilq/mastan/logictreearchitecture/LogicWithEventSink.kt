package com.hadilq.mastan.logictreearchitecture

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers

/**
 * Provide an [eventSink] and [state] for [Composable]s.
 */
interface LogicWithEventSink<S, E> {

    @Composable
    fun Update(
        beforeUpdateState: S,
        event: E,
        onState: (S) -> Unit,
        content: @Composable (state: S, event: E, onState: (S) -> Unit) -> Unit,
    )

    val state: S

    /**
     * Call it with [Dispatchers.Main].
     */
    val eventSink: suspend (E) -> Unit
}
