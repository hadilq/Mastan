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
package com.hadilq.mastan.auth.data

import android.app.Application
import com.hadilq.mastan.*
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import org.mobilenativefoundation.store.cache5.CacheBuilder
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject


interface UserManager {
    fun userComponentFor(accessTokenRequest: AccessTokenRequest): UserComponent
    fun userComponentFor(code: String): UserComponent?
}

@ContributesTo(AppScope::class)
interface UserManagerProvider {
    fun getUserManager(): UserManager
}

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealUserManager @Inject constructor(private val userParentComponentProvider: UserParentComponentProvider) : UserManager {
    private val cache = CacheBuilder<String, UserComponent>()
        .build()
    private val atomicReference = AtomicReference<UserComponent>()


    override fun userComponentFor(accessTokenRequest: AccessTokenRequest): UserComponent {
        return cache.getOrPut(accessTokenRequest.code) {
            userParentComponentProvider.component.createUserComponent()
                .userComponent(accessTokenRequest = accessTokenRequest)
        }.also {
            atomicReference.set(it)
        }
    }

    override fun userComponentFor(code: String): UserComponent? {
        return cache.getIfPresent(code)
    }
}