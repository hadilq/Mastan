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
package com.hadilq.mastan.tabselector

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.theme.LocalThemeOutput


data class Tab(val name: String, val image: Int, val onClick: () -> Unit)

@Composable
fun TabSelectorDetail(
    tabs: List<Tab>,
    selectedIndex: Int,
    screenHeight: Int,
) {
    LazyVerticalGrid(
        // on below line we are setting the
        // column count for our grid view.
        columns = GridCells.Fixed(3),
        // on below line we are adding padding
        // from all sides to our grid view.

        modifier = Modifier
            .width((screenHeight).dp)
            .height(160.dp)
            .clip(RoundedCornerShape(4.dp))

    ) {
        itemsIndexed(tabs) { index, collection ->
            TabGrid(collection, index, selectedIndex)
        }
    }

}

@Composable
private fun TabGrid(
    tab: Tab,
    index: Int,
    selectedIndex: Int
) {
    Column() {

        val color1 = colorScheme.tertiary
        val color2 = colorScheme.onTertiary

        val gradient = when (index % 2) {
            0 -> listOf(color1, color2) //was gradient
            else -> listOf(color1, color2) //was gradient
        }
        TabContent(
            tab = tab
        )
    }
    Spacer(Modifier.height(4.dp))
}


private val MinImageSize = 134.dp
private val CategoryShape = RoundedCornerShape(10.dp)
private const val CategoryTextProportion = 0.55f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabContent(
    tab: Tab
) {
    val dim = LocalThemeOutput.current.dim
    AssistChip(
        modifier =  Modifier
            .height(80.dp)
            .width(130.dp)
            .padding(horizontal = dim.paddingSize0_5, vertical = dim.paddingSize0_5),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = colorScheme.surface,
            leadingIconContentColor = colorScheme.secondary, labelColor = colorScheme.primary
        ),
        shape = RoundedCornerShape(25, 25, 25, 25),
        onClick = tab.onClick,
        label = {

            Text(
                modifier=Modifier.wrapContentSize(),
                text = tab.name,
                style = MaterialTheme.typography.labelLarge.copy(color = colorScheme.primary),
                maxLines = 1
            )
        },
        leadingIcon = {
            Image(
                modifier = Modifier.size(dim.paddingSize4),
                painter = painterResource(tab.image),
                contentDescription = "",
                colorFilter = ColorFilter.tint(colorScheme.secondary),
            )

        }
    )

}

