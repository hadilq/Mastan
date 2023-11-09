package com.hadilq.mastan.ui.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

fun <T: Any> LazyListScope.lazyItems(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = { index ->  items[index]?.let { key?.invoke(it) } ?: "" }
    ) { index ->
        itemContent(items[index])
    }
}
