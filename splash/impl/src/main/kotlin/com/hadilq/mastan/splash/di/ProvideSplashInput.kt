package com.hadilq.mastan.splash.di

import com.hadilq.mastan.splash.SplashInput
import kotlin.reflect.KProperty0

val splashInput: SplashInput
    get() = dependencyProvider()

private lateinit var dependencyProvider: () -> SplashInput

fun setSplashInputProvider(provider: KProperty0<SplashInput>) {
    if (::dependencyProvider.isInitialized) {
        /**
         * This is not gonna happen in the production, because the initialization of dependencies
         * is happening in the Application::onCreate method, so any future problem will be detectable
         * as soon as the app is launched.
         */
        throw IllegalAccessException("splash input is already set before!")
    }
    dependencyProvider = provider::get
}
