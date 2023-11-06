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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.hadilq.mastan.auth.AuthLogicIo
import com.hadilq.mastan.datastore.DataStoreLogicIo
import com.hadilq.mastan.log.LogLogicIo
import com.hadilq.mastan.log.LogUiIo
import com.hadilq.mastan.log.RealLogLogicIo
import com.hadilq.mastan.log.RealLogUiIo
import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture
import com.hadilq.mastan.loginflow.LoginFlowLogicIo
import com.hadilq.mastan.loginflow.LoginFlowUiIo
import com.hadilq.mastan.loginflow.RealLoginFlowLogicIo
import com.hadilq.mastan.loginflow.RealLoginFlowUiIo
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.navigation.NavigationUiIo
import com.hadilq.mastan.navigation.RealNavigationLogicIo
import com.hadilq.mastan.navigation.RealNavigationUiIo
import com.hadilq.mastan.network.NetworkLogicIo
import com.hadilq.mastan.root.InitialRootEvent
import com.hadilq.mastan.root.RootDataStore
import com.hadilq.mastan.root.RootDependencies
import com.hadilq.mastan.root.RootEvent
import com.hadilq.mastan.root.RootState
import com.hadilq.mastan.root.initRootState
import com.hadilq.mastan.theme.MastanThemeUiIo

class RealRootDependencies(
    private val dataStoreLogicIo: DataStoreLogicIo,
    override val authLogicIo: AuthLogicIo,
    override val logicTreeArchitecture: LogicTreeArchitecture,
    override val navigationLogicIo: NavigationLogicIo,
    override val mastanThemeUiIo: MastanThemeUiIo,
    override val networkLogicIo: NetworkLogicIo,
    override val logLogicIo: LogLogicIo,
) : RootDependencies {

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val state: MutableState<RootState> by logicTreeArchitecture.singleWithNoRace {
        mutableStateOf(initRootState)
    }

    /**
     * It's a single instance in [AppDependencies] scope.
     */
    override val event: MutableState<RootEvent> by logicTreeArchitecture.singleWithNoRace {
        mutableStateOf(InitialRootEvent)
    }

    /**
     * It's a single instance in [RootDependencies] scope.
     */
    override val navigationUiIo: NavigationUiIo by logicTreeArchitecture.singleWithNoRace {
        RealNavigationUiIo()
    }

    /**
     * It's a single instance in [RootDependencies] scope.
     */
    override val rootDataStore: RootDataStore by logicTreeArchitecture.singleWithNoRace {
        RealRootDataStore(dataStoreLogicIo)
    }

    /**
     * It's a single instance in [RootDependencies] scope.
     */
    override val loginFlowUiIo: LoginFlowUiIo by logicTreeArchitecture.singleWithNoRace {
        RealLoginFlowUiIo()
    }

    /**
     * It's a single instance in [RootDependencies] scope.
     */
    override val loginFlowLogicIo: LoginFlowLogicIo by logicTreeArchitecture.singleWithNoRace {
        RealLoginFlowLogicIo()
    }

    /**
     * It's a single instance in [RootDependencies] scope.
     */
    override val logUiIo: LogUiIo by logicTreeArchitecture.singleWithNoRace {
        RealLogUiIo(true, true)
    }
}
