package com.hadilq.mastan.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hadilq.mastan.auth.LocalAuthLogicIo
import com.hadilq.mastan.auth.NoAuthEvent
import com.hadilq.mastan.auth.NoAuthState
import com.hadilq.mastan.log.LocalLogLogicIo
import com.hadilq.mastan.logictreearchitecture.LocalLogicTreeArchitecture
import com.hadilq.mastan.loginflow.InitialLoginFlowState
import com.hadilq.mastan.loginflow.LocalLoginFlowLogicIo
import com.hadilq.mastan.navigation.LegacyMainContent
import com.hadilq.mastan.navigation.LocalNavigationLogicIo
import com.hadilq.mastan.navigation.LoginFlow
import com.hadilq.mastan.navigation.NavigationState
import com.hadilq.mastan.navigation.NoNavigationEvent
import com.hadilq.mastan.network.LocalNetworkLogicIo
import com.hadilq.mastan.theme.NoMastanThemeEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@Composable
fun RealRootLogic() {
    var rootState by remember { rootDependencies.state }
    var rootEvent by remember { rootDependencies.event }
    CompositionLocalProvider(
        LocalNavigationLogicIo provides rootDependencies.navigationLogicIo,
        LocalAuthLogicIo provides rootDependencies.authLogicIo,
        LocalLoginFlowLogicIo provides rootDependencies.loginFlowLogicIo,
        LocalNetworkLogicIo provides rootDependencies.networkLogicIo,
        LocalLogicTreeArchitecture provides rootDependencies.logicTreeArchitecture,
        LocalLogLogicIo provides rootDependencies.logLogicIo,
    ) {
        val logger = rootDependencies.logLogicIo

        rootDependencies.navigationLogicIo.navigationLogic(
            rootState.navigationState,
            when (val event = rootEvent) {
                is NavigationRootEvent -> event.navigationEvent
                else -> NoNavigationEvent
            }
        ) {
            if (rootEvent !is NoRootEvent) {
                rootEvent = NoRootEvent
            }
            logger.logDebug { "root update by navigation $rootState, $it" }
            rootState = rootState.copy(navigationState = it)
        }

        rootDependencies.authLogicIo.authLogic(
            rootState.authState,
            when (val event = rootEvent) {
                is AuthRootEvent -> event.authEvent
                else -> NoAuthEvent
            }
        ) {
            if (rootEvent !is NoRootEvent) {
                rootEvent = NoRootEvent
            }
            logger.logDebug { "root update by auth $rootState, $it" }
            rootState = rootState.copy(authState = it)
        }

        rootDependencies.mastanThemeUiIo.mastanThemeLogic(
            rootState.themeState,
            when (val event = rootEvent) {
                is ThemeRootEvent -> event.themeEvent
                else -> NoMastanThemeEvent
            }
        ) {
            if (rootEvent !is NoRootEvent) {
                rootEvent = NoRootEvent
            }
            logger.logDebug { "root update by theme $rootState, $it" }
            rootState = rootState.copy(themeState = it)
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val loaded = rootDependencies.rootDataStore.load()
            if (loaded.isSuccess) {
                val state = loaded.getOrThrow()
                rootState = if (state.authState !is NoAuthState &&
                    (state.navigationState.stack.isEmpty() ||
                        (state.navigationState.stack.size == 1 &&
                            state.navigationState.stack.first() == LoginFlow(InitialLoginFlowState)))
                ) {
                    state.copy(navigationState = NavigationState(listOf(LegacyMainContent)))
                } else {
                    state
                }
            }
        }
    }

    LaunchedEffect(rootState) {
        delay(1.seconds.inWholeMilliseconds)
        withContext(Dispatchers.IO) {
            rootDependencies.rootDataStore.save(rootState)
        }
    }
}