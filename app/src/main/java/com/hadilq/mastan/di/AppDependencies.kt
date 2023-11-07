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
import com.hadilq.mastan.auth.RealAuthLogicIo
import com.hadilq.mastan.datastore.DataStoreLogicIo
import com.hadilq.mastan.datastore.RealDataStoreLogicIo
import com.hadilq.mastan.log.LogLogicIo
import com.hadilq.mastan.log.RealLogLogicIo
import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.RealLogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.di.ImplDependencies
import com.hadilq.mastan.molecule
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.navigation.RealNavigationLogicIo
import com.hadilq.mastan.network.NetworkDependencies
import com.hadilq.mastan.network.NetworkLogicIo
import com.hadilq.mastan.network.RealNetworkDependencies
import com.hadilq.mastan.network.RealNetworkLogicIo
import com.hadilq.mastan.root.RealRootLogicIo
import com.hadilq.mastan.root.RealRootUiIo
import com.hadilq.mastan.root.RootDependencies
import com.hadilq.mastan.root.RootLogicIo
import com.hadilq.mastan.root.RootUiIo
import com.hadilq.mastan.splash.RealSplashOutUiIo
import com.hadilq.mastan.splash.SplashDependencies
import com.hadilq.mastan.splash.SplashUiIo
import com.hadilq.mastan.theme.MastanThemeUiIo
import com.hadilq.mastan.theme.RealMastanThemeUiIo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

/**
 * This is the input of `app` module, which are the dependencies of this module on other modules and functionalities.
 */
@ImplDependencies
interface AppDependencies {
    val rootLogicIo: RootLogicIo
    val rootUiIo: RootUiIo
    val buildConfigDetails: BuildConfigDetails
    val networkDependencies: NetworkDependencies
    val networkLogicIo: NetworkLogicIo
    val legacyDependencies: LegacyDependencies
    val authLogicIo: AuthLogicIo
    val splashDependencies: SplashDependencies
    val mastanThemeUiIo: MastanThemeUiIo
    val splashUiIo: SplashUiIo
    val navigationLogicIo: NavigationLogicIo
    val rootDependencies: RootDependencies
    val molecule: Job
    val dataStoreLogicIo: DataStoreLogicIo
}

class RealAppDependencies(
    private val application: Application,

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    private val logLogicIo: LogLogicIo = RealLogLogicIo(true, true),

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    private val logicTreeArchitecture: LogicTreeArchitecture = RealLogicTreeArchitecture(logLogicIo),
) : AppDependencies {

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val legacyDependencies: LegacyDependencies by logicTreeArchitecture.singleWithNoRace {
        RealLegacyDependencies(
            application = application,
            mastanThemeUiIo = mastanThemeUiIo,
            splashUiIo = splashUiIo,
            networkLogicIo = networkLogicIo,
            navigationLogicIo = navigationLogicIo,
            rootLogicIo = rootLogicIo,
            rootUiIo = rootUiIo,
            authLogicIo = authLogicIo,
        )
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val buildConfigDetails: BuildConfigDetails by logicTreeArchitecture.singleWithNoRace {
        RealBuildConfigDetails()
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val networkDependencies: NetworkDependencies by logicTreeArchitecture.singleWithNoRace {
        RealNetworkDependencies(buildConfigDetails.debug, logicTreeArchitecture)
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val networkLogicIo: NetworkLogicIo by logicTreeArchitecture.singleWithNoRace {
        RealNetworkLogicIo(logLogicIo, authLogicIo, logicTreeArchitecture, networkDependencies)
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val authLogicIo: AuthLogicIo by logicTreeArchitecture.single {
        RealAuthLogicIo(logicTreeArchitecture)
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val splashDependencies: SplashDependencies by logicTreeArchitecture.singleWithNoRace {
        RealSplashDependencies()
    }

    override val mastanThemeUiIo: MastanThemeUiIo by logicTreeArchitecture.singleWithNoRace {
        RealMastanThemeUiIo(logicTreeArchitecture)
    }

    override val splashUiIo: SplashUiIo by logicTreeArchitecture.singleWithNoRace {
        RealSplashOutUiIo()
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val navigationLogicIo: NavigationLogicIo by logicTreeArchitecture.singleWithNoRace {
        RealNavigationLogicIo(logicTreeArchitecture)
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val rootDependencies: RootDependencies by logicTreeArchitecture.singleWithNoRace {
        RealRootDependencies(
            dataStoreLogicIo = dataStoreLogicIo,
            authLogicIo = authLogicIo,
            logicTreeArchitecture = logicTreeArchitecture,
            navigationLogicIo = navigationLogicIo,
            mastanThemeUiIo = mastanThemeUiIo,
            networkLogicIo = networkLogicIo,
            logLogicIo = logLogicIo,
        )
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val rootLogicIo: RootLogicIo by logicTreeArchitecture.singleWithNoRace {
        RealRootLogicIo()
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val rootUiIo: RootUiIo by logicTreeArchitecture.singleWithNoRace {
        RealRootUiIo()
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val molecule: Job by logicTreeArchitecture.singleWithNoRace {
        molecule(this)
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val dataStoreLogicIo: DataStoreLogicIo by logicTreeArchitecture.singleWithNoRace {
        RealDataStoreLogicIo(logicTreeArchitecture, application.applicationContext, logLogicIo)
    }

}
