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
package com.hadilq.mastan

import android.app.Application
import com.hadilq.mastan.auth.AuthLogicIo
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.network.NetworkLogicIo
import com.hadilq.mastan.root.RootLogicIo
import com.hadilq.mastan.root.RootUiIo
import com.hadilq.mastan.splash.SplashUiIo
import com.hadilq.mastan.theme.MastanThemeUiIo

/**
 * This is the input of `legacy` module, which are the dependencies of this module on other modules and functionalities.
 */
interface LegacyDependencies {
    val application: Application

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val mastanThemeUiIo: MastanThemeUiIo

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val splashUiIo: SplashUiIo

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val networkLogicIo: NetworkLogicIo

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val navigationLogicIo: NavigationLogicIo

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val rootLogicIo: RootLogicIo

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val rootUiIo: RootUiIo

    /**
     * It's a single instance in [LegacyDependencies] scope.
     */
    val authLogicIo: AuthLogicIo
}
