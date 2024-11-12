package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem

@Composable
fun FoodGrid(
  searchKeyword: String,
  like: Int,
  dislike: Int,
  filteredFoodList: List<FoodItem>,
  onSearchKeywordChange: (String) -> Unit,
  onClickFoodItem: (FoodItem) -> Unit,
  modifier: Modifier = Modifier,
  templateName: String? = null,
  gridState: LazyGridState = rememberLazyGridState()
) {
  Box(
    modifier = modifier.fillMaxSize()
  ) {
    if (templateName != null) {
      TemplateTitle(
        name = templateName,
        like = like,
        dislike = dislike,
        modifier = Modifier.fillMaxWidth(),
        gridState = gridState
      )
    }
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      modifier = Modifier.fillMaxWidth(),
      state = gridState,
      contentPadding = PaddingValues(top = 104.dp, start = 8.dp, end = 8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      item(span = { GridItemSpan(3) }) {
        LunchVoteTextField(
          text = searchKeyword,
          onTextChange = onSearchKeywordChange,
          hintText = stringResource(R.string.add_template_hint_text),
          modifier = Modifier.padding(bottom = 8.dp),
          leadingIcon = { SearchIcon() }
        )
      }

      items(filteredFoodList) { foodItem ->
        FoodItem(
          foodItem = foodItem,
          onClick = { onClickFoodItem(foodItem) }
        )
      }
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    FoodGrid(
      templateName = "학생회 회식 대표 메뉴",
      searchKeyword = "치킨",
      like = 3,
      dislike = 2,
      filteredFoodList = List(32) { FoodItem() },
      onSearchKeywordChange = {},
      onClickFoodItem = {}
    )
  }
}