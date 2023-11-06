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
package com.hadilq.mastan.auth.test

import androidx.compose.ui.test.junit4.createComposeRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.auth.AddUserAuthEvent
import com.hadilq.mastan.auth.AuthState
import com.hadilq.mastan.auth.LoggedInAccountsState
import com.hadilq.mastan.auth.NoAuthState
import com.hadilq.mastan.auth.RealAuthLogic
import com.hadilq.mastan.auth.RemoveUserAuthEvent
import com.hadilq.mastan.auth.RemoveUserByDetailsAuthEvent
import com.hadilq.mastan.auth.Server
import com.hadilq.mastan.auth.User
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RealAuthLogicTestLogicIo {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `logged-in add-user`() = runTest {
        val server = Server(domain = "testdomain")
        val user = givenUser(server)
        var result: AuthState = LoggedInAccountsState(mapOf(server.domain to server), user)
        composeTestRule.setContent {
            RealAuthLogic(
                state = result,
                event = AddUserAuthEvent(user),
                onState = { result = it }
            )
        }
        composeTestRule.awaitIdle()
        assertThat(result).isEqualTo(
            LoggedInAccountsState(
                mapOf(server.domain to server.copy(users = mapOf(user.accessTokenRequest.code to user))),
                user
            )
        )
    }

    @Test
    fun `no-auth add-user`() = runTest {
        val server = Server(domain = "testdomain")
        val user = givenUser(server)
        var result: AuthState = NoAuthState
        composeTestRule.setContent {
            RealAuthLogic(
                state = result,
                event = AddUserAuthEvent(user),
                onState = { result = it }
            )
        }
        composeTestRule.awaitIdle()
        assertThat(result).isEqualTo(
            LoggedInAccountsState(
                mapOf(server.domain to server.copy(users = mapOf(user.accessTokenRequest.code to user))),
                user
            )
        )
    }

    @Test
    fun `logged-in add-user as update`() = runTest {
        val server = givenServer()
        val user2 = server.users.values.first().copy(accessToken = "updated access token")
        var result: AuthState = LoggedInAccountsState(
            servers = mapOf(server.domain to server),
            user2,
        )
        composeTestRule.setContent {
            RealAuthLogic(
                state = result,
                event = AddUserAuthEvent(user2),
                onState = { result = it }
            )
        }
        composeTestRule.awaitIdle()
        assertThat(result).isEqualTo(
            LoggedInAccountsState(
                servers = mapOf(
                    user2.domain to Server(
                        domain = user2.domain,
                        users = mapOf(user2.accessTokenRequest.code to user2)
                    )
                ),
                user2
            )
        )
    }

    @Test
    fun `logged-in remove-user-by-details`() = runTest {
        val server = Server(domain = "testdomain")
        val user = givenUser(server)
        var result: AuthState = LoggedInAccountsState(
            mapOf(server.domain to server.copy(users = mapOf(user.accessTokenRequest.code to user))),
            user
        )

        composeTestRule.setContent {
            RealAuthLogic(
                state = result,
                event = RemoveUserByDetailsAuthEvent(server.domain, user.accessTokenRequest.code),
                onState = { result = it }
            )
        }
        composeTestRule.awaitIdle()
        assertThat(result).isEqualTo(NoAuthState)
    }

    @Test
    fun `logged-in remove-user`() = runTest {
        val server = Server(domain = "testdomain")
        val user = givenUser(server)
        var result: AuthState = LoggedInAccountsState(
            mapOf(server.domain to server.copy(users = mapOf(user.accessTokenRequest.code to user))),
            user
        )

        composeTestRule.setContent {
            RealAuthLogic(
                state = result,
                event = RemoveUserAuthEvent(user),
                onState = { result = it }
            )
        }
        composeTestRule.awaitIdle()
        assertThat(result).isEqualTo(NoAuthState)
    }

    private fun givenServer(): Server {
        val domain = "some domain"
        val code = "some code"
        return Server(
            domain = domain,
            users = mapOf(
                code to User(
                    accessTokenRequest = givenAccessTokenRequest(domain, code),
                    domain = domain,
                    accessToken = "access token"
                )
            )
        )
    }


    private fun givenUser(
        server: Server,
    ) = User(givenAccessTokenRequest(server.domain), server.domain, "access token")

    private fun givenAccessTokenRequest(domain: String, code: String = "code") = AccessTokenRequest(
        domain,
        "cliendId",
        "clientSecret",
        "https://sldfjvv",
        code,
        "authorization code",
        "scope"
    )
}