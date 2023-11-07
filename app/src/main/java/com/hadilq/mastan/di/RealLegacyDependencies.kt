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
package com.hadilq.mastan.di

import android.app.Application
import com.hadilq.mastan.LegacyDependencies
import com.hadilq.mastan.auth.AuthLogicIo
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.network.NetworkLogicIo
import com.hadilq.mastan.root.RootLogicIo
import com.hadilq.mastan.root.RootUiIo
import com.hadilq.mastan.splash.SplashUiIo
import com.hadilq.mastan.theme.MastanThemeUiIo

class RealLegacyDependencies(
    override val application: Application,
    override val mastanThemeUiIo: MastanThemeUiIo,
    override val splashUiIo: SplashUiIo,
    override val networkLogicIo: NetworkLogicIo,
    override val navigationLogicIo: NavigationLogicIo,
    override val rootLogicIo: RootLogicIo,
    override val rootUiIo: RootUiIo,
    override val authLogicIo: AuthLogicIo,
) : LegacyDependencies
