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

import androidx.compose.runtime.compositionLocalOf
import com.hadilq.mastan.logictreearchitecture.di.LogicIoDefinition

val LocalRootLogicIo = compositionLocalOf<RootLogicIo> { error("No component found!") }

/**
 * This is the definition of `root` module. Its implementation is available in the `:root:impl` module.
 * This indicates the functionality that `root` module provides for other modules.
 */
@LogicIoDefinition
interface RootLogicIo {
    val rootLogic: RootLogic
}
