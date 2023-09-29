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
package com.hadilq.mastan.search

import androidx.compose.material3.ColorScheme
import androidx.paging.ExperimentalPagingApi
import com.hadilq.mastan.AuthRequiredScope
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.auth.data.OauthRepository
import com.hadilq.mastan.shared.UserApi
import com.hadilq.mastan.theme.Dimension
import com.hadilq.mastan.timeline.data.Account
import com.hadilq.mastan.timeline.data.AccountRepository
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.data.Tag
import com.hadilq.mastan.timeline.data.mapStatus
import com.hadilq.mastan.timeline.data.toStatusDb
import com.hadilq.mastan.timeline.ui.model.UI
import com.hadilq.mastan.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import org.mobilenativefoundation.store.store5.ResponseOrigin
import org.mobilenativefoundation.store.store5.StoreResponse
import java.util.Locale
import javax.inject.Inject


abstract class SearchPresenter :
    Presenter<SearchPresenter.SearchEvent, SearchPresenter.SearchModel, SearchPresenter.SearchEffect>(
        SearchModel(emptyList())
    ) {


    sealed interface SearchEvent

    //    object Load : searchEvent
    data class Init(val colorScheme: ColorScheme, val dim: Dimension) : SearchEvent


    data class SearchModel(
        val accounts: List<Account>? = null,
        val hashtags: List<Tag>? = null,
        val statuses: List<UI>? = null,
        val isLoading: Boolean = false,
        val account: Account? = null,
        val error: String? = null
    )

    sealed interface SearchEffect

    abstract fun onQueryTextChange(searchTerm: String)
}


@ExperimentalPagingApi
@ContributesBinding(AuthRequiredScope::class, boundType = SearchPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealSearchPresenter @Inject constructor(
    private val searchRepository: SearchRepository,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val accountRepository: AccountRepository,

) : SearchPresenter() {
    //Drop 1 keeps from emitting the initial value
    private val searchInput: MutableStateFlow<String> = MutableStateFlow("")

    override suspend fun eventHandler(event: SearchEvent, scope: CoroutineScope) {
        when (event) {
            is Init -> {
                model = model.copy(account = accountRepository.getCurrent())
                mapSearchToResults(event.colorScheme, event.dim)
            }

        }
    }

    suspend fun mapSearchToResults(colorScheme: ColorScheme, dim: Dimension) {
        searchInput
            .debounce(100)
            .filter { lengthGreaterThan1(it) }
            .flatMapLatest(searchRepository::data)
            .flowOn(Dispatchers.IO)
            .catch { model = SearchModel(error = it.localizedMessage) }
            .collect { results ->
                when (results) {
                    is StoreResponse.Data -> {
                        if (results.origin == ResponseOrigin.Fetcher && results.dataOrNull()
                            == null
                        )
                            model = SearchModel(error = "Sorry no results found")
                        else {
                            val authHeader = oauthRepository.getAuthHeader()
                            val searchResults = results.requireData()
                            val userTags =
                                kotlin.runCatching {
                                    userApi.followedTags(authHeader = authHeader)

                                }
                            val tags =
                                (userTags.getOrNull() ?: emptyList<Tag>()).toSet().map { it.name }

                            model = SearchModel(
                                accounts = searchResults.accounts,
                                hashtags = searchResults.hashtags.map {
                                    if (tags.contains(it.name)) {
                                        it.copy(isFollowing = true)
                                    } else {
                                        it
                                    }
                                },
                                statuses = searchResults.statuses.map {
                                    it.toStatusDb(FeedType.User).mapStatus(colorScheme, dim)
                                })
                        }
                    }

                    is StoreResponse.Loading -> {
                        model = model.copy(isLoading = true)
                    }

                    else -> {
                        if (results is StoreResponse.Error.Message) {
                            model = SearchModel(error = results.errorMessageOrNull())
                        } else if (results is StoreResponse.Error) {
                            model = model.copy(isLoading = false)
                        }
                    }
                }
            }
    }

    private fun lengthGreaterThan1(it: String): Boolean {
        if (it.length <= 1) model = SearchModel()
        return it.length > 1
    }


    override fun onQueryTextChange(searchTerm: String) {
        searchInput.tryEmit(searchTerm.lowercase(Locale.getDefault()))
    }
}