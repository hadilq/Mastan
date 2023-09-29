package com.hadilq.mastan.splash

class RealSplashOut: SplashOutput {
    override val splashActivityRes: Int
        get() = R.layout.activity_splash
    override val motionLayoutRes: Int
        get() = R.id.motionLayout
}
