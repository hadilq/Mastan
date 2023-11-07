package com.hadilq.mastan.loginflow

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
sealed interface LoginFlowState

@Stable
@Serializable
sealed interface ServerSelectState : LoginFlowState

@Stable
@Serializable
object InitialLoginFlowState : ServerSelectState

@Stable
@Serializable
data class ServerErrorServerSelectState(
    val error: String,
) : ServerSelectState

@Stable
@Serializable
sealed interface SignInState : LoginFlowState

@Stable
@Serializable
data class ServerSignInState(
    val serverDomain: String,
) : SignInState

@Stable
@Serializable
data class CreatedApplicationSignInState(
    val domain: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val oauthAuthorizeUrl: String,
): SignInState

@Stable
@Serializable
data class CreatedApplicationErrorSignInState(
    val serverDomain: String,
    val error: String,
): SignInState

@Stable
@Serializable
data class OAuthFailedToSignInState(
    val serverDomain: String,
    val error: String,
): SignInState
