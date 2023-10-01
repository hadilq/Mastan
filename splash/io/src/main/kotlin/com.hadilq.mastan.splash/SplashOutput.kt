package com.hadilq.mastan.splash

/**
 * This is the output of `splash` module. Its implementation is available in the `:splash:impl` module.
 * This indicates the functionality that `splash` module provides for other modules.
 */
interface SplashOutput {
    val splashActivityRes: Int
    val motionLayoutRes: Int
}
