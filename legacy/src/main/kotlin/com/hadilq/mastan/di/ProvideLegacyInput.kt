package com.hadilq.mastan.di

import com.hadilq.mastan.LegacyInput
import kotlin.reflect.KProperty0

val legacyInput: LegacyInput
    get() = dependencyProvider()

private lateinit var dependencyProvider: () -> LegacyInput

fun setLegacyInputProvider(provider: KProperty0<LegacyInput>) {
    if (::dependencyProvider.isInitialized) {
        /**
         * This is not gonna happen in the production, because the initialization of dependencies
         * is happening in the Application::onCreate method, so any future problem will be detectable
         * as soon as the app is launched.
         */
        throw IllegalAccessException("legacy input is already set before!")
    }
    dependencyProvider = provider::get
}
