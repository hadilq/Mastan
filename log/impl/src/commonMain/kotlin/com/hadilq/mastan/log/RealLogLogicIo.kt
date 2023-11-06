package com.hadilq.mastan.log

class RealLogLogicIo(
    private val debugAllowed: Boolean,
    private val warningAllowed: Boolean,
): LogLogicIo {
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

    override fun wrongState(message: () -> String) {
        if (debugAllowed) {
            TODO("wrong state: ${message()}")
        }
    }
}