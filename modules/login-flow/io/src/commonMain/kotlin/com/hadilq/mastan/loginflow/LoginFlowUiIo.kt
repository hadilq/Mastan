package com.hadilq.mastan.loginflow

import androidx.compose.runtime.compositionLocalOf
import com.hadilq.mastan.logictreearchitecture.di.UiIoDefinition

val LocalLoginFlowUiIo = compositionLocalOf<LoginFlowUiIo> { error("No component found!") }

/**
 * This is the definition of `login-flow` module. Its implementation is available in the `:login-flow:impl` module.
 * This indicates the functionality that `login-flow` module provides for other modules.
 */
@UiIoDefinition
interface LoginFlowUiIo {
    val loginFlowUi: LoginFlowUi
}