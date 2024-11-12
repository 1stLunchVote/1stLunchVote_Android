package com.jwd.lunchvote.presentation.screen.template.add_template

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.screen.friends.FriendListContract.FriendListEvent
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.FoodGrid
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.SearchIcon
import com.jwd.lunchvote.presentation.widget.TemplateTitle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTemplateRoute(
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AddTemplateViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is AddTemplateSideEffect.PopBackStack -> popBackStack()
        is AddTemplateSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(AddTemplateEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else AddTemplateScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun AddTemplateScreen(
  state: AddTemplateState,
  modifier: Modifier = Modifier,
  onEvent: (AddTemplateEvent) -> Unit = {},
) {
  Screen(
    modifier = modifier
      .padding(horizontal = 24.dp)
      .padding(top = 8.dp),
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.add_template_title),
        popBackStack = { onEvent(AddTemplateEvent.OnClickBackButton) }
      )
    },
    actions = {
      if (state.foodItemList.any { it.status != FoodItem.Status.DEFAULT }) {
        FloatingActionButton(
          onClick = { onEvent(AddTemplateEvent.OnClickAddButton) },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
          Text(
            text = stringResource(R.string.add_template_add_button),
            modifier = Modifier.padding(horizontal = 24.dp)
          )
        }
      }
    },
    scrollable = false
  ) {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
      val gridState = rememberLazyGridState()
      TemplateTitle(
        name = state.name,
        like = state.foodItemList.count { it.status == FoodItem.Status.LIKE },
        dislike = state.foodItemList.count { it.status == FoodItem.Status.DISLIKE },
        modifier = Modifier.fillMaxWidth(),
        gridState = gridState
      )
      FoodGrid(
        searchKeyword = state.searchKeyword,
        filteredFoodList = state.foodItemList.filter { it.food.name.contains(state.searchKeyword) },
        onSearchKeywordChange = { onEvent(AddTemplateEvent.OnSearchKeywordChange(it)) },
        onClickFoodItem = { onEvent(AddTemplateEvent.OnClickFoodItem(it)) },
        gridState = gridState,
        topPadding = 104.dp,
        bottomPadding = 104.dp
      )
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    AddTemplateScreen(
      AddTemplateState(
        name = "학생회 회식 대표 메뉴",
        foodItemList = List(32) {
          FoodItem(
            food = FoodUIModel(name = "${it}번째 음식"),
            status = FoodItem.Status.LIKE
          )
        }
      )
    )
  }
}