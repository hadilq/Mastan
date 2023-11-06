package com.hadilq.mastan.logictreearchitecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class FakeLogicWithEventSink<S, E>(
    private val defaultState: S,
    private val noEvent: E,
) : LogicWithEventSink<S, E> {

    val sunkEvent: MutableState<E> = mutableStateOf(noEvent)
    var lastEvent: E = noEvent

    @Composable
    override fun Update(
        beforeUpdateState: S, event: E, onState: (S) -> Unit,
        content: @Composable (beforeUpdateState: S, event: E, onState: (S) -> Unit) -> Unit,
    ) {
        val (latestState, latestEvent) = if (lastEvent != sunkEvent.value) {
            lastEvent = sunkEvent.value
            state to sunkEvent.value
        } else {
            lastEvent = event
            sunkEvent.value = event
            beforeUpdateState to event
        }
        content(latestState, latestEvent) {
            state = it
            onState(it)
        }
    }

    override var state: S = defaultState

    override val eventSink: suspend (E) -> Unit = { event ->
        delay(150.milliseconds.inWholeMilliseconds)
        sunkEvent.value = event
    }
}
