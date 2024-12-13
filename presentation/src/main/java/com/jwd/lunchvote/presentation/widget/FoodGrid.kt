package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.widget.TextFieldIconDefaults.SearchIcon

@Composable
internal fun FoodGrid(
  searchKeyword: String,
  filteredFoodList: List<FoodItem>,
  onSearchKeywordChange: (String) -> Unit,
  onClickFoodItem: (FoodItem) -> Unit,
  modifier: Modifier = Modifier,
  gridState: LazyGridState = rememberLazyGridState(),
  topPadding: Dp = FoodGridDefaults.topPadding(),
  bottomPadding: Dp = FoodGridDefaults.bottomPadding()
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = modifier.fillMaxWidth(),
    state = gridState,
    contentPadding = PaddingValues(top = topPadding, bottom = bottomPadding),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    item(span = { GridItemSpan(3) }) {
      TextField(
        text = searchKeyword,
        onTextChange = onSearchKeywordChange,
        hintText = stringResource(R.string.add_template_hint_text),
        modifier = Modifier.padding(bottom = 4.dp),
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

internal object FoodGridDefaults {

  fun topPadding(titleExpended: Boolean = false) = if (titleExpended) 120.dp else 72.dp
  fun bottomPadding(buttonExists: Boolean = false) = if (buttonExists) 104.dp else 0.dp

  val ZeroPadding = 0.dp

  val DummyFoodList = List(32) {
    FoodItem(
      food = FoodItemDefaults.dummyFood(it),
      status = if (it % 4 == 0) FoodItem.Status.LIKE
        else if (it % 5 == 0) FoodItem.Status.DISLIKE
        else FoodItem.Status.DEFAULT
    )
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    FoodGrid(
      searchKeyword = "",
      filteredFoodList = FoodGridDefaults.DummyFoodList,
      onSearchKeywordChange = {},
      onClickFoodItem = {},
      modifier = Modifier.padding(24.dp),
      topPadding = FoodGridDefaults.ZeroPadding,
      bottomPadding = FoodGridDefaults.ZeroPadding
    )
  }
}