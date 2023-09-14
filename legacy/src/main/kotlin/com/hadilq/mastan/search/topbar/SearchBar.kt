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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.theme.PaddingSize0_5

/**
 * Composable used for getting search input from a user
 *
 * A parent composable is expected to pass in a deconstructed [SearchState]
 * and handle the following functionality:
 * [onQueryChange] - called anytime a user types something in [BasicTextField],
 * caller should handle [query] which is the input text
 * [onSearchFocusChange] - called when search field is focused
 * [onClearQuery] - called when clear icon is clicked
 * [onSearchAll] - called when Search All icon clicked
 */
@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    searchFocused: Boolean,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    modifier: Modifier = Modifier,
    onSearchAll: () -> Unit
) {
  val searchBarHeight = 56.dp
  val paddingHalf = PaddingSize0_5
  Surface(
      modifier = modifier
          .fillMaxWidth()
//          .background(Color.Red)
          .height(searchBarHeight)
          .padding(top = 4.dp, bottom = 4.dp, end = 8.dp)
          .clip(RoundedCornerShape(4.dp))
  ) {
    Box(Modifier.fillMaxSize()) {
//      if (!searchFocused) SearchHint()
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
              .fillMaxSize()
              .wrapContentHeight()
      ) {
        SearchField(query, onQueryChange, onSearchFocusChange)
        ProgressIndicator(searching)
        ClearButton(onClearQuery, searchFocused, query)
//        SearchAllButton(onSearchAll, query)
      }
    }
  }
}




