package com.hadilq.mastan.loginflow

import androidx.compose.runtime.compositionLocalOf
import com.hadilq.mastan.logictreearchitecture.di.LogicIoDefinition

val LocalLoginFlowLogicIo = compositionLocalOf<LoginFlowLogicIo> { error("No component found!") }

/**
 * This is the output of `login-flow` module. Its implementation is available in the `:login-flow:impl` module.
 * This indicates the functionality that `login-flow` module provides for other modules.
 */
@LogicIoDefinition
interface LoginFlowLogicIo {
    val loginFlowLogic: LoginFlowLogic
}