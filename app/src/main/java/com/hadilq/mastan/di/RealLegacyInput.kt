package com.hadilq.mastan.di

import android.app.Application
import com.hadilq.mastan.LegacyInput
import com.hadilq.mastan.theme.RealThemeOutput
import com.hadilq.mastan.theme.ThemeOutput

class RealLegacyInput(
    override val application: Application
) : LegacyInput {
    override val themeOutput: ThemeOutput by lazy(LazyThreadSafetyMode.NONE) {
        RealThemeOutput()
    }
}
