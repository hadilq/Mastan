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

import com.hadilq.mastan.auth.AuthEvent
import com.hadilq.mastan.navigation.NavigationEvent
import com.hadilq.mastan.theme.MastanThemeEvent

sealed interface RootEvent

object NoRootEvent : RootEvent

data class NavigationRootEvent(val navigationEvent: NavigationEvent) : RootEvent

data class AuthRootEvent(val authEvent: AuthEvent) : RootEvent

data class ThemeRootEvent(val themeEvent: MastanThemeEvent) : RootEvent

object InitialRootEvent : RootEvent
