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
package com.androiddev.social


import android.app.Application
import com.androiddev.social.auth.data.AccessTokenRequest
import com.androiddev.social.auth.data.OauthRepository
import com.androiddev.social.shared.UserApi
import com.androiddev.social.timeline.ui.UrlHandlerMediator
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
import kotlin.reflect.KClass

class App : Application() {
    val component by lazy {
        (DaggerSkeletonComponent.factory()
            .create(this as Application, this) as AppComponent.AppParentComponent).appComponent()
    }

    override fun onCreate() {
        super.onCreate()
    }
}

@MergeComponent(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
interface SkeletonComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app2: Application,
            @BindsInstance app: App,
        ): SkeletonComponent
    }

    fun urlHandlerMediator(): UrlHandlerMediator
}

@ContributesSubcomponent(
    scope = AppScope::class,
    parentScope = SkeletonScope::class,
)
@SingleIn(AppScope::class)
interface AppComponent {
    @ContributesTo(SkeletonScope::class)
    interface AppParentComponent {
        fun appComponent(): AppComponent
    }

    fun urlHandlerMediator(): UrlHandlerMediator
}

@ContributesSubcomponent(
    scope = UserScope::class,
    parentScope = AppScope::class
)
@SingleIn(UserScope::class)
interface UserComponent {
    @ContributesSubcomponent.Factory
    interface Factory {
        fun userComponent(
            @BindsInstance accessTokenRequest: AccessTokenRequest
        ): UserComponent
    }

    fun oauthRepository(): OauthRepository
    fun api(): UserApi
    fun request(): AccessTokenRequest
    fun urlHandlerMediator(): UrlHandlerMediator
}

@ContributesTo(AppScope::class)
interface UserParentComponent {
    fun createUserComponent(): UserComponent.Factory
}

@ContributesSubcomponent(
    scope = AuthRequiredScope::class,
    parentScope = UserScope::class
)
@SingleIn(AuthRequiredScope::class)
interface AuthRequiredComponent {

    @ContributesTo(UserScope::class)
    interface ParentComponent {
        fun createAuthRequiredComponent(): AuthRequiredComponent
    }
}

interface Injector {
//    abstract fun signInPresenter(): Any
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthOptionalScope::class,
    parentScope = AppScope::class
)
@SingleIn(AuthOptionalScope::class)
interface AuthOptionalComponent : Injector {
    @ContributesTo(AppScope::class)
    interface ParentComponent {
        fun createAuthOptionalComponent(): AuthOptionalComponent
    }
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthOptionalScreenScope::class,
    parentScope = AppScope::class
)
@SingleIn(AuthOptionalScreenScope::class)
interface AuthOptionalScreenComponent : Injector {
    @ContributesTo(AppScope::class)
    interface ScreenParentComponent : Injector {
        fun createAuthOptionalScreenComponent(): AuthOptionalScreenComponent
    }
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthRequiredScreenScope::class,
    parentScope = UserScope::class
)
@SingleIn(AuthRequiredScreenScope::class)
interface AuthRequiredScreenComponent : Injector {
    @ContributesTo(UserScope::class)
    interface ScreenParentComponent : Injector {
        fun createAuthRequiredScreenComponent(): AuthRequiredScreenComponent
    }
}

abstract class SkeletonScope private constructor()
abstract class AppScope private constructor()
abstract class UserScope private constructor()
abstract class AuthRequiredScope private constructor()
abstract class AuthOptionalScope private constructor()
abstract class AuthOptionalScreenScope private constructor()
abstract class AuthRequiredScreenScope private constructor()

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleIn(val clazz: KClass<*>)
