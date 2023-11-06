/**
 * Copyright 2023 Hadi Lashkari Ghouchani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hadilq.mastan.root

import androidx.compose.runtime.MutableState
import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.di.ImplDependencies
import com.hadilq.mastan.auth.AuthLogicIo
import com.hadilq.mastan.log.LogLogicIo
import com.hadilq.mastan.log.LogUiIo
import com.hadilq.mastan.loginflow.LoginFlowLogicIo
import com.hadilq.mastan.loginflow.LoginFlowUiIo
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.navigation.NavigationUiIo
import com.hadilq.mastan.network.NetworkLogicIo
import com.hadilq.mastan.theme.MastanThemeUiIo
import kotlin.reflect.KProperty0

@ImplDependencies
interface RootDependencies {
    val state: MutableState<RootState>
    val event: MutableState<RootEvent>
    val logicTreeArchitecture: LogicTreeArchitecture
    val navigationLogicIo: NavigationLogicIo
    val navigationUiIo: NavigationUiIo
    val authLogicIo: AuthLogicIo
    val rootDataStore: RootDataStore
    val mastanThemeUiIo: MastanThemeUiIo
    val loginFlowLogicIo: LoginFlowLogicIo
    val loginFlowUiIo: LoginFlowUiIo
    val networkLogicIo: NetworkLogicIo
    val logLogicIo: LogLogicIo
    val logUiIo: LogUiIo
}

val rootDependencies: RootDependencies
    get() = dependencyProvider()

private lateinit var dependencyProvider: () -> RootDependencies

fun setRootDependenciesProvider(provider: KProperty0<RootDependencies>) {
    if (::dependencyProvider.isInitialized) {
        /**
         * This is not gonna happen in the production, because the initialization of dependencies
         * is happening in the Application::onCreate method, so any future problem will be detectable
         * as soon as the app is launched.
         */
        throw IllegalAccessException("root input is already set before!")
    }
    dependencyProvider = provider::get
}
