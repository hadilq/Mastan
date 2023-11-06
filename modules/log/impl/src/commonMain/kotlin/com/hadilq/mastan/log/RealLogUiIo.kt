package com.hadilq.mastan.log

class RealLogUiIo(
    private val debugAllowed: Boolean,
    private val warningAllowed: Boolean
): LogUiIo {

    override fun logDebug(message: String) {
        if (debugAllowed) {
            println("QQQ d: $message")
        }
    }

    override fun logDebug(message: () -> String) {
        if (debugAllowed) {
            logDebug(message())
        }
    }

    override fun logWarning(message: String) {
        if (warningAllowed) {
            println("QQQ w: $message")
        }
    }

    override fun logWarning(message: () -> String) {
        if (warningAllowed) {
            logWarning(message())
        }
    }
}