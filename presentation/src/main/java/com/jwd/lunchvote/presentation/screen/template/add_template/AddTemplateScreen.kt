package com.jwd.lunchvote.presentation.screen.template.add_template

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.SearchIcon
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
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.add_template_title),
        popBackStack = { onEvent(AddTemplateEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      TemplateTitle(
        name = state.name,
        like = state.foodItemList.count { it.status == FoodItem.Status.LIKE },
        dislike = state.foodItemList.count { it.status == FoodItem.Status.DISLIKE },
        modifier = Modifier.fillMaxWidth()
      )
      LunchVoteTextField(
        text = state.searchKeyword,
        onTextChange = { onEvent(AddTemplateEvent.OnSearchKeywordChange(it)) },
        hintText = stringResource(R.string.add_template_hint_text),
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { SearchIcon() }
      )
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val filteredFoodList = state.foodItemList.filter { it.food.name.contains(state.searchKeyword) }

        items(filteredFoodList) { foodItem ->
          FoodItem(
            foodItem = foodItem,
            onClick = { onEvent(AddTemplateEvent.OnClickFoodItem(foodItem)) }
          )
        }
      }
      Button(
        onClick = { onEvent(AddTemplateEvent.OnClickAddButton) },
        modifier = Modifier.align(CenterHorizontally),
        enabled = state.foodItemList.any { it.status != FoodItem.Status.DEFAULT }
      ) {
        Text(text = stringResource(R.string.add_template_add_button))
      }
    }
  }
}

@Composable
private fun TemplateTitle(
  name: String,
  like: Int,
  dislike: Int,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .clip(MaterialTheme.shapes.small)
      .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.shapes.small)
      .padding(vertical = 20.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = CenterHorizontally
  ) {
    Text(
      text = name,
      style = MaterialTheme.typography.bodyLarge
    )
    LikeDislike(like, dislike)
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    AddTemplateScreen(
      AddTemplateState(
        name = "학생회 회식 대표 메뉴",
        foodItemList = List(10) {
          FoodItem(
            food = FoodUIModel(name = "${it}번째 음식")
          )
        }
      )
    )
  }
}