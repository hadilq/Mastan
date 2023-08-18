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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.hadilq.mastan.search.SearchPresenter.SearchModel
import com.hadilq.mastan.search.results.SearchResults
import com.hadilq.mastan.search.topbar.TopBar
import com.hadilq.mastan.theme.PaddingSize1
import com.hadilq.mastan.timeline.ui.BottomSheetContent
import com.hadilq.mastan.timeline.ui.BottomSheetContentProvider
import com.hadilq.mastan.timeline.ui.UriPresenter
import com.hadilq.mastan.timeline.ui.model.UI


/**
 * [SearchScreen] and [SearchView] are separated for testability.
 * Think of [SearchScreen] as the coordinator of state and callbacks
 * This allows [SearchView] to stay pure, accepting only state properties and event callbacks
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    model: SearchModel,
    navController: NavController,
    uriPresenter: UriPresenter,
    onQueryChange: (String) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    goToConversation: (UI) -> Unit,
) {
    val bottomState: ModalBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val bottomSheetContentProvider = remember { BottomSheetContentProvider(bottomState) }

    SearchView(
        state = model,
        navController = navController,
        uriPresenter = uriPresenter,
        bottomSheetContentProvider = bottomSheetContentProvider,
        onQueryChange = onQueryChange,
        goToProfile = goToProfile,
        goToTag = goToTag,
        goToConversation = goToConversation,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchView(
    state: SearchModel,
    navController: NavController,
    uriPresenter: UriPresenter,
    bottomSheetContentProvider: BottomSheetContentProvider,
    onQueryChange: (String) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    goToConversation: (UI) -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetContentProvider.bottomState,
        sheetShape = RoundedCornerShape(topStart = PaddingSize1, topEnd = PaddingSize1),
        sheetContent = {
            BottomSheetContent(
                bottomSheetContentProvider = bottomSheetContentProvider,
                onShareStatus = {},
                onDelete = { statusId -> },
                onMessageSent = {},
                goToProfile = goToProfile,
                goToTag = goToTag,
                goToConversation = goToConversation,
                onMuteAccount = {},
                onBlockAccount = {},
            )
        },
    ) {
        val searchState: SearchState = rememberSearchState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(navController, onQueryChange = onQueryChange, state.isLoading, searchState)
            }
        )
        {
            SearchResults(
                model = state,
                goToBottomSheet = bottomSheetContentProvider::showContent,
                goToProfile = goToProfile,
                goToTag = goToTag,
                goToConversation = goToConversation,
                onOpenURI = { uri, type ->
                    uriPresenter.handle(UriPresenter.Open(uri, type))
                },
            )
        }
    }
}


//@Preview
//@Composable
//fun SearchLoadingPreview() = SearchView(state = SearchState(isLoading = true), onQueryChange = {},)
//
//
//@Preview
//@Composable
//fun SearchErrorPreview() = SearchView(
//    state = SearchState(
//        error = stringResource(
//            R.string.error_string
//        )
//    ),
//    onQueryChange = {},
//)

