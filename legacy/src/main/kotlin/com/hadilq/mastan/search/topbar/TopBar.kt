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
package com.hadilq.mastan.search.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hadilq.mastan.search.SearchState

@Composable
fun TopBar(
    navController: NavController,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean,
    state: SearchState
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.surface,
        title = {  state.searching = isLoading
            SearchBar(
                query = state.query,
                onQueryChange = {
                    state.query = it
                    onQueryChange(it.text)
                },
                searchFocused = state.focused,
                onSearchFocusChange = { state.focused = it },
                onClearQuery = { state.query = TextFieldValue(""); },
                searching = state.searching,
                onSearchAll = { }
            ) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurface,
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "search"
                    )
                }
            } else {
                null
            }
        },
        modifier = Modifier.background(Color.White).height(40.dp),
        actions = {
        }
    )
}

