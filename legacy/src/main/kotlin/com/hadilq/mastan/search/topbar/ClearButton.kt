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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Icon Button which expects caller to handle [onClearQuery] when clicked
 * Since compose doesn't have a View.Hide, we set alpha to 0 when "hiding"
 * This keeps rest of layout from jumping
 */
@Composable
fun ClearButton(onClearQuery: () -> Unit, searchFocused: Boolean, query: TextFieldValue) {
    val alpha: Float by animateFloatAsState(if (searchFocused && query.text.isNotEmpty()) 1f else 0f,
        label = ""
    )
    IconButton(onClick = onClearQuery, modifier = Modifier.alpha(alpha)) {
        Icon(
            imageVector = Icons.Rounded.Clear,
            contentDescription = "Clear Icon",
            tint = MaterialTheme.colorScheme.primary,

            )
    }
}

