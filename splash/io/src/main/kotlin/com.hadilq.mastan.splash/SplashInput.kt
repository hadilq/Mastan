package com.hadilq.mastan.splash

import android.app.Activity

/**
 * This is the input of `splash` module. Its implementation is available in the `:splash:impl` module.
 * This indicates the dependencies of this module on other modules and functionalities.
 */
interface SplashInput {
    fun mainActivityClass(): Class<Activity>
}
