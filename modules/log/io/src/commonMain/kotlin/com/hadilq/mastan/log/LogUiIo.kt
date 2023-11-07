package com.hadilq.mastan.log

import androidx.compose.runtime.compositionLocalOf
import com.hadilq.mastan.logictreearchitecture.di.UiIoDefinition

val LocalLogUiIo = compositionLocalOf<LogUiIo> { error("No component found!") }

@UiIoDefinition
interface LogUiIo {

    /**
     * Log debug with a message that doesn't need process.
     */
    fun logDebug(message: String)

    /**
     * Log debug with a message that needs to be processed.
     */
    fun logDebug(message: () -> String)

    /**
     * Log warning with a message that doesn't need process.
     */
    fun logWarning(message: String)

    /**
     * Log warning with a message that needs to be processed.
     */
    fun logWarning(message: () -> String)
}