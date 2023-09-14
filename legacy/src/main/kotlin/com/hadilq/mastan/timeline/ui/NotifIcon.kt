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
package com.hadilq.mastan.timeline.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.legacy.R

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun Search(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .rotate(-30f),
            painter = painterResource(R.drawable.search),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        )
//        DropdownMenu(
//            offset = DpOffset(0.dp, 10.dp),
//            expanded = showSearch,
//            onDismissRequest = { showSearch = false },
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(
//                    MaterialTheme.colorScheme.primary.copy(alpha = .5f)
//                )
//        ) {
////            var searchText by remember { mutableStateOf("Search") }
////            SearchBar(searchText, "Placeholder",
////                { searchText = it }, { searchText = "" })
//        }
//    }
//}
//
//@ExperimentalMaterial3Api
//@ExperimentalAnimationApi
//@ExperimentalComposeUiApi
//@Composable
//fun SearchBar(
//    searchText: String,
//    placeholderText: String = "",
//    onSearchTextChanged: (String) -> Unit = {},
//    onClearClick: () -> Unit = {},
//    onImageOnly: () -> Unit = {},
//    onLinksOnly: () -> Unit = {},
//    onBoostedOnly: () -> Unit = {},
//) {
//    var showClearButton by remember { mutableStateOf(false) }
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val focusRequester = remember { FocusRequester() }
//    Column {
//        Row {
//            OutlinedTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 2.dp)
//                    .onFocusChanged { focusState ->
//                        showClearButton = (focusState.isFocused)
//                    }
//                    .focusRequester(focusRequester),
//                value = searchText,
//                onValueChange = onSearchTextChanged,
//                placeholder = {
//                    Text(text = placeholderText)
//                },
//                colors = TextFieldDefaults.textFieldColors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                trailingIcon = {
//                    AnimatedVisibility(
//                        visible = showClearButton,
//                        enter = fadeIn(),
//                        exit = fadeOut()
//                    ) {
//                        IconButton(onClick = { onClearClick() }) {
//                            Icon(
//                                imageVector = Icons.Filled.Close,
//                                contentDescription = "search"
//                            )
//                        }
//
//                    }
//                },
//                maxLines = 1,
//                singleLine = true,
//                keyboardOptions = KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
//                keyboardActions = KeyboardActions(onDone = {
//                    keyboardController?.hide()
//                }),
//            )
//
//
//            LaunchedEffect(Unit) {
//                focusRequester.requestFocus()
//            }
//
//        }
//        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
//            IconButton(
//                modifier = Modifier.padding(8.dp),
//
//                onClick = onImageOnly
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.filter),
//                    contentDescription = "",
//                    colorFilter = ColorFilter.tint(Color.White),
//                )
//            }
//            IconButton(
//                modifier = Modifier.padding(8.dp),
//                onClick = onLinksOnly
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.media),
//                    contentDescription = "",
//                    colorFilter = ColorFilter.tint(Color.White),
//                )
//            }
//            IconButton(
//                modifier = Modifier.padding(8.dp),
//                onClick = onBoostedOnly
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.link),
//                    contentDescription = "",
//                    colorFilter = ColorFilter.tint(Color.White),
//                )
//            }
//            IconButton(
//                modifier = Modifier.padding(8.dp),
//                onClick = onBoostedOnly
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.rocket3),
//                    contentDescription = "",
//                    colorFilter = ColorFilter.tint(Color.White),
//                )
//            }
//        }
//    }
    }
}

