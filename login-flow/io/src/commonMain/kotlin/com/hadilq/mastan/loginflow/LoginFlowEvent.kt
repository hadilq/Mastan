package com.hadilq.mastan.loginflow

import java.net.URI

sealed interface LoginFlowEvent

object NoLoginFlowEvent : LoginFlowEvent

sealed interface ServerSelectEvent : LoginFlowEvent

data class NextPageServerSelectEvent(val domain: String) : ServerSelectEvent

sealed interface SignInEvent : LoginFlowEvent

object BackSignInEvent : SignInEvent

object WebViewCancelSignInEvent : SignInEvent

data class ResultUriSignInEvent(val uri: URI) : SignInEvent

object OAuthFailedRetrySignInEvent : SignInEvent

object CreatedApplicationErrorRetrySignInEvent : SignInEvent

