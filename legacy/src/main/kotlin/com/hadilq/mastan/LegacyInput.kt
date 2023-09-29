package com.hadilq.mastan

import android.app.Application
import com.hadilq.mastan.theme.ThemeOutput


interface LegacyInput {
    val application: Application

    /**
     * It's a single instance in [LegacyInput] scope.
     */
    val themeOutput: ThemeOutput
}
