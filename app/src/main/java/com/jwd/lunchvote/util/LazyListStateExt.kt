package com.jwd.lunchvote.util

import androidx.compose.foundation.lazy.LazyListState

val LazyListState.wasLastItemVisible: Boolean
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 3