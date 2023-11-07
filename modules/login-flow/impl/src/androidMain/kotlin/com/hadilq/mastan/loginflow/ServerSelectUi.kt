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
package com.hadilq.mastan.loginflow

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.loginflow.imp.R
import com.hadilq.mastan.theme.LocalMastanThemeUiIo

@Composable
fun ServerSelectUi(
    state: ServerSelectState,
    onEvent: (ServerSelectEvent) -> Unit,
) {
    val localTheme = LocalMastanThemeUiIo.current
    val dim = localTheme.dim
    localTheme.mastanThemeUi {
        var current by remember { mutableStateOf("") }
        Surface(
            modifier = Modifier,
            color = colorScheme.background
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(2f)
                    .blur(2.dp)
                    .alpha(.5f),
                painter = painterResource(id = R.drawable.serverselectbackground),
                contentDescription = null
            )

            Column(
                Modifier
                    .padding(dim.paddingSize2)
                    .fillMaxWidth(1f)
                    .fillMaxHeight()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center


            ) {
                Text(
                    color = colorScheme.primary,
                    modifier = Modifier.padding(bottom = dim.paddingSize2),
                    text = "Welcome to Mastan",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    color = colorScheme.primary,
                    text = "Enter Server Name",
                    style = MaterialTheme.typography.headlineMedium
                )
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                }

                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester = focusRequester)
                        .wrapContentHeight()
                        .fillMaxWidth(.99f)
                        .padding(
                            top = dim.paddingSize8,
                            start = dim.paddingSize1,
                            end = dim.paddingSize1
                        ),
                    textStyle = LocalTextStyle.current,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = colorScheme.onSurface.copy(alpha = .8f),
                        cursorColor = Color.Black,
                        disabledLabelColor = colorScheme.onSecondaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = colorScheme.surface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    value = current,
                    onValueChange = {
                        current = it
                    },
                    trailingIcon = {
                        Icon(Icons.Default.Clear,
                            contentDescription = "clear text",
                            modifier = Modifier.clickable {
                                current = ""
                            })
                    },
                    singleLine = true,
                )

                if (state is ServerErrorServerSelectState) {
                    Text(
                        modifier = Modifier
                            .padding(dim.paddingSize1, 0.dp),
                        color = colorScheme.error,
                        text = state.error,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Box(
                    modifier = Modifier
//                        .alpha(.8f)
                        .fillMaxWidth()

                ) {
                    ExtendedFloatingActionButton(backgroundColor = colorScheme.primary,
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Center)
                            .padding(dim.paddingSize3),
                        text = {
                            Text("Continue to Server")
                        },
                        onClick = { onEvent(NextPageServerSelectEvent(current)) }
                    )
                }
            }
        }
    }
}
