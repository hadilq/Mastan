package com.hadilq.mastan.logictreearchitecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.hadilq.mastan.log.LogLogicIo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RealLogicWithEventSink<S, E>(
    private val logger: LogLogicIo,
    defaultState: S,
    noEvent: E,
) : LogicWithEventSink<S, E> {

    private val sunkEvent: MutableState<E> = mutableStateOf(noEvent)
    private var lastEvent: E = noEvent

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
        logger.logDebug { "RealLogicWithEventSink Update $latestState, $latestEvent" }
        state = latestState
        content(latestState, latestEvent) {
            logger.logDebug { "RealLogicWithEventSink Update via onState $state, $it" }
            state = it
            onState(it)
        }
    }

    override var state: S = defaultState

    override val eventSink: suspend (E) -> Unit = { event ->
        delay(10.milliseconds.inWholeMilliseconds)
        withContext(Dispatchers.Main) {
            sunkEvent.value = event
        }
    }
}
