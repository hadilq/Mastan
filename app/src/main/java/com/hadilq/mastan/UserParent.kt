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
import com.hadilq.mastan.timeline.ui.UrlHandlerMediator
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

class UserParent : Application(), UserParentComponentProvider, AuthOptionalParentComponentProvider {

    val component: AppComponent by lazy(LazyThreadSafetyMode.NONE) {
        (DaggerSkeletonComponent.factory()
            .create(this as Application, this, this, this) as AppComponent.AppParentComponent).appComponent()
    }

    override val userParentComponent: UserParentComponent by lazy(LazyThreadSafetyMode.NONE) {
        component as UserParentComponent
    }

    override val authOptionalParentComponent: AuthOptionalComponent.AuthOptionalParentComponent by lazy(LazyThreadSafetyMode.NONE) {
        component as AuthOptionalComponent.AuthOptionalParentComponent
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
            @BindsInstance app: UserParent,
            @BindsInstance userParentComponentProvider: UserParentComponentProvider,
            @BindsInstance authOptionalParentComponentProvider: AuthOptionalParentComponentProvider,
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

    fun userParentComponentProvider(): UserParentComponentProvider
}

