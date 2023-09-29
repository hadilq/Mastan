package com.hadilq.mastan.di

import android.app.Application
import com.hadilq.mastan.LegacyInput
import com.hadilq.mastan.splash.SplashInput

class AppInput(
    private val application: Application,
) {
    /**
     * It's a single instance in [AppInput] scope.
     */
    val legacyInput: LegacyInput by lazy(LazyThreadSafetyMode.NONE) {
        RealLegacyInput(application)
    }

    /**
     * It's a single instance in [AppInput] scope.
     */
    val splashInput: SplashInput by lazy(LazyThreadSafetyMode.NONE) {
        RealSplashInput()
    }
}
