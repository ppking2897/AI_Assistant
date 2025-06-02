package com.bianca.ai_assistant.utils

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ScrollBottomNavigation(listState: LazyListState) {
    val bottomBarHeightPx = with(LocalDensity.current) { 56.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()

    val needAdjustScroll by remember {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val totalItems = listState.layoutInfo.totalItemsCount
            if (visibleItems.isNotEmpty() && visibleItems.last().index == totalItems - 1) {
                val lastItem = visibleItems.last()
                val viewportEnd = listState.layoutInfo.viewportEndOffset
                (lastItem.offset + lastItem.size > viewportEnd - bottomBarHeightPx)
            } else false
        }
    }

    LaunchedEffect(needAdjustScroll) {
        if (needAdjustScroll) {
            coroutineScope.launch {
                listState.scrollBy(bottomBarHeightPx)
            }
        }
    }
}