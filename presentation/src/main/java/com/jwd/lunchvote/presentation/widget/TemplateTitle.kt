package com.jwd.lunchvote.presentation.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun TemplateTitle(
  name: String,
  like: Int,
  dislike: Int,
  modifier: Modifier = Modifier,
  gridState: LazyGridState? = null
) {
  var isExpanded by remember { mutableStateOf(true) }

  if (gridState != null) {
    LaunchedEffect(gridState) {
      snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
        .collect { (index, offset) ->
          isExpanded = index == 0 && offset < 48.dp.value
        }
    }
  }

  AnimatedContent(
    targetState = isExpanded,
    modifier = modifier
      .shadow(8.dp, MaterialTheme.shapes.small)
      .clip(MaterialTheme.shapes.small)
      .background(MaterialTheme.colorScheme.background)
      .border(2.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
      .padding(horizontal = 20.dp)
      .zIndex(1f),
    label = "TemplateTitle"
  ) { expended ->
    if (expended) Column(
      modifier = modifier.height(96.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = name,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge
      )
      LikeDislike(like, dislike)
    }
    else Row(
      modifier = modifier.height(56.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = name,
        modifier = Modifier.weight(1f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge
      )
      LikeDislike(like, dislike)
    }
  }
}

@Composable
@Preview
private fun Preview() {
  ScreenPreview {
    Box(
      modifier = Modifier.padding(24.dp)
    ) {
      val gridState = rememberLazyGridState()
      TemplateTitle(
        name = "학생회 회식하면 항상 빠지지 않는 대표 메뉴",
        like = 10,
        dislike = 5,
        modifier = Modifier.fillMaxWidth(),
        gridState = gridState
      )
      FoodGrid(
        searchKeyword = "",
        filteredFoodList = FoodGridDefaults.DummyFoodList,
        onSearchKeywordChange = {},
        onClickFoodItem = {},
        gridState = gridState,
        topPadding = FoodGridDefaults.topPadding(true)
      )
    }
  }
}