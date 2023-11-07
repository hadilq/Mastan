package com.hadilq.mastan

import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.auth.User
import com.hadilq.mastan.network.UserApi
import com.hadilq.mastan.timeline.ui.UrlHandlerMediator
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import dagger.BindsInstance
import javax.inject.Scope
import kotlin.reflect.KClass

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

    fun api(): UserApi
    fun accessTokenRequest(): AccessTokenRequest
    fun urlHandlerMediator(): UrlHandlerMediator
}

@ContributesTo(AppScope::class)
interface UserParentComponent {
    fun createUserComponent(): UserComponent.Factory
}

interface UserParentComponentProvider {
    val userParentComponent: UserParentComponent
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
    interface AuthOptionalParentComponent {
        fun createAuthOptionalComponent(): AuthOptionalComponent
    }
}

interface AuthOptionalParentComponentProvider {
    val authOptionalParentComponent: AuthOptionalComponent.AuthOptionalParentComponent
}

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
