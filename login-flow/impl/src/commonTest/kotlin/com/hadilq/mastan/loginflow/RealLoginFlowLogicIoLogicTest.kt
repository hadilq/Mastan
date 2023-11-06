package com.hadilq.mastan.loginflow

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.log.FakeLogLogicIo
import com.hadilq.mastan.log.LocalLogLogicIo
import com.hadilq.mastan.log.LogLogicIo
import com.hadilq.mastan.logictreearchitecture.FakeLogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.LocalLogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture
import com.hadilq.mastan.navigation.FakeNavigationLogicIo
import com.hadilq.mastan.navigation.LocalNavigationLogicIo
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.navigation.TimeLineNavigationEvent
import com.hadilq.mastan.network.FakeAuthRepository
import com.hadilq.mastan.network.FakeNetworkLogicIo
import com.hadilq.mastan.network.LocalNetworkLogicIo
import com.hadilq.mastan.network.NetworkLogicIo
import com.hadilq.mastan.network.OauthApplicationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URI

@RunWith(RobolectricTestRunner::class)
class RealLoginFlowLogicIoLogicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `login-flow blank domain`() = runTest {
        val result = wrapCallingRealLoginFlowLogic(
            initialState = InitialLoginFlowState,
            event = NextPageServerSelectEvent(" ")
        )
        assertThat(result.size).isEqualTo(1)
        assertThat(result.first()).isEqualTo(ServerErrorServerSelectState("Domain cannot be blank!"))
    }

    @Test
    fun `login-flow not blank domain`() = runTest {
        val getAppTokenResponse = givenAppTokenResponse()
        val network = FakeNetworkLogicIo(
            authRepository = FakeAuthRepository(getAppTokenResponse = getAppTokenResponse)
        )

        val domain = "notblankdomain"
        val result = wrapCallingRealLoginFlowLogic(
            networkLogicIo = network,
            initialState = InitialLoginFlowState,
            event = NextPageServerSelectEvent(domain)
        )
        assertThat(result.size).isEqualTo(2)
        assertThat(result.first()).isEqualTo(ServerSignInState(domain))
        assertThat(result[1]).isEqualTo(
            CreatedApplicationSignInState(
                domain = domain,
                redirectUri = getAppTokenResponse.redirectUri,
                clientSecret = getAppTokenResponse.clientSecret,
                clientId = getAppTokenResponse.clientId,
                oauthAuthorizeUrl = "https://notblankdomain/oauth/authorize?client_id=givenClientId&scope=read+write+follow+push&redirect_uri=givenRedirectUri&response_type=code",
            )
        )
    }

    @Test
    fun `login-flow ResultUriSignInEvent with error`() = runTest {
        val initialState = givenCreatedApplicationSignInState()
        val expectedErrorMessage = "givenErrorMessage"
        val result = wrapCallingRealLoginFlowLogic(
            initialState = initialState,
            event = ResultUriSignInEvent(URI("?error=$expectedErrorMessage"))
        )
        assertThat(result.size).isEqualTo(1)
        assertThat(result.first()).isEqualTo(
            OAuthFailedToSignInState(
                serverDomain = initialState.domain,
                error = expectedErrorMessage,
            )
        )
    }

    @Test
    fun `login-flow ResultUriSignInEvent with code with error`() = runTest {
        val authRepository = FakeAuthRepository()
        val network = FakeNetworkLogicIo(
            authRepository = authRepository
        )
        val navigation = FakeNavigationLogicIo()

        val initialState = givenCreatedApplicationSignInState()
        val expectedErrorMessage = "givenCode"
        val result = wrapCallingRealLoginFlowLogic(
            networkLogicIo = network,
            navigationLogicIo = navigation,
            initialState = initialState,
            event = ResultUriSignInEvent(URI("?code=$expectedErrorMessage"))
        )
        assertThat(authRepository.createAccessTokenCalls.size).isEqualTo(1)
        assertThat(authRepository.createAccessTokenCalls.first()).isEqualTo(
            AccessTokenRequest(
                code = expectedErrorMessage,
                clientId = initialState.clientId,
                clientSecret = initialState.clientSecret,
                redirectUri = initialState.redirectUri,
                domain = initialState.domain,
            )
        )
        assertThat(result.size).isEqualTo(1)
        assertThat(result.first()).isEqualTo(
            OAuthFailedToSignInState(
                serverDomain = initialState.domain,
                error = "createAccessTokenResponse is error!",
            )
        )
    }

    @Test
    fun `login-flow ResultUriSignInEvent with code with success`() = runTest {
        val authRepository = FakeAuthRepository(createAccessTokenResponse = true)
        val network = FakeNetworkLogicIo(
            authRepository = authRepository
        )
        val navigation = FakeNavigationLogicIo()

        val initialState = givenCreatedApplicationSignInState()
        val expectedErrorMessage = "givenCode"
        wrapCallingRealLoginFlowLogic(
            networkLogicIo = network,
            navigationLogicIo = navigation,
            initialState = initialState,
            event = ResultUriSignInEvent(URI("?code=$expectedErrorMessage"))
        )
        assertThat(authRepository.createAccessTokenCalls.size).isEqualTo(1)
        assertThat(authRepository.createAccessTokenCalls.first()).isEqualTo(
            AccessTokenRequest(
                code = expectedErrorMessage,
                clientId = initialState.clientId,
                clientSecret = initialState.clientSecret,
                redirectUri = initialState.redirectUri,
                domain = initialState.domain,
            )
        )
        assertThat(navigation.events.size).isEqualTo(1)
        assertThat(navigation.events.first()).isEqualTo(TimeLineNavigationEvent)
    }

    private suspend fun CoroutineScope.wrapCallingRealLoginFlowLogic(
        logicTreeArchitecture: LogicTreeArchitecture = FakeLogicTreeArchitecture(this),
        networkLogicIo: NetworkLogicIo = FakeNetworkLogicIo(),
        navigationLogicIo: NavigationLogicIo = FakeNavigationLogicIo(),
        logLogicIo: LogLogicIo = FakeLogLogicIo(),
        initialState: LoginFlowState,
        event: LoginFlowEvent,
    ): List<LoginFlowState> {
        val result = mutableListOf<LoginFlowState>()
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalNetworkLogicIo provides networkLogicIo,
                LocalNavigationLogicIo provides navigationLogicIo,
                LocalLogicTreeArchitecture provides logicTreeArchitecture,
                LocalLogLogicIo provides logLogicIo,
            ) {
                RealLoginFlowLogic(
                    state = initialState,
                    event = event,
                    onState = { result.add(it) }
                )
            }
        }
        composeTestRule.awaitIdle()
        return result
    }

    private fun givenCreatedApplicationSignInState(): CreatedApplicationSignInState {
        return givenAppTokenResponse().let {
            CreatedApplicationSignInState(
                domain = "givendomain",
                redirectUri = it.redirectUri,
                clientId = it.clientId,
                clientSecret = it.clientSecret,
                oauthAuthorizeUrl = "",
            )
        }
    }

    private fun givenAppTokenResponse(): OauthApplicationResponse =
        OauthApplicationResponse(
            clientId = "givenClientId",
            clientSecret = "givenClientSecret",
            redirectUri = "givenRedirectUri",
        )
}