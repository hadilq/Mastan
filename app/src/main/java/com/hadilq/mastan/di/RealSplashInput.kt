package com.hadilq.mastan.di

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.ExperimentalTextApi
import com.hadilq.mastan.splash.SplashInput
import com.hadilq.mastan.timeline.ui.MainActivity

class RealSplashInput : SplashInput {
    @OptIn(
        ExperimentalTextApi::class, ExperimentalMaterialApi::class,
        ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun mainActivityClass(): Class<Activity> =
        MainActivity::class.java as Class<Activity>
}
