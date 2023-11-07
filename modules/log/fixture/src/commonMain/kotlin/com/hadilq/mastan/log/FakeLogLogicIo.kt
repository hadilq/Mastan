package com.hadilq.mastan.log

class FakeLogLogicIo : LogLogicIo {

    var message: String = ""

    override fun logDebug(message: String) {
        this.message = message
        println("d: $message")
    }

    override fun logDebug(message: () -> String) {
        logDebug(message())
    }

    override fun logWarning(message: String) {
        this.message = message
        println("w: $message")
    }

    override fun logWarning(message: () -> String) {
        logWarning(message())
    }

    override fun wrongState(message: () -> String) {
        TODO("wrong state ${message()}")
    }
}