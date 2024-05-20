package com.jwd.lunchvote.presentation.ui.vote.first

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.FoodStatus
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.ProgressTopBar
import com.jwd.lunchvote.presentation.widget.StepProgress
import com.jwd.lunchvote.presentation.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstVoteRoute(
  navigateToSecondVote: () -> Unit,
  openTemplateDialog: () -> Unit,
  openVoteExitDialog: () -> Unit,
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: FirstVoteViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val firstVoteState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is FirstVoteSideEffect.NavigateToSecondVote -> navigateToSecondVote()
        is FirstVoteSideEffect.OpenTemplateDialog -> openTemplateDialog()
        is FirstVoteSideEffect.OpenVoteExitDialog -> openVoteExitDialog()
        is FirstVoteSideEffect.PopBackStack -> popBackStack()
        is FirstVoteSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  BackHandler {
    viewModel.sendEvent(FirstVoteEvent.OnClickExitButton)
  }

  if (isLoading) LoadingScreen()
  else FirstVoteScreen(
    firstVoteState = firstVoteState,
    modifier = modifier,
    onClickFood = { food -> viewModel.sendEvent(FirstVoteEvent.OnClickFood(food)) },
    setSearchKeyword = { search -> viewModel.sendEvent(FirstVoteEvent.SetSearchKeyword(search)) },
    navigateToSecondVote = navigateToSecondVote
  )
}

@Composable
fun FirstVoteScreen(
  firstVoteState: FirstVoteState,
  modifier: Modifier = Modifier,
  onClickFood: (FoodUIModel) -> Unit = {},
  setSearchKeyword: (String) -> Unit = {},
  navigateToSecondVote: () -> Unit = {},
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    ProgressTopBar(
      title = stringResource(R.string.first_vote_title),
      onProgressComplete = { navigateToSecondVote() }
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      LikeDislike(firstVoteState.likeList.size, firstVoteState.dislikeList.size)
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (i in 1..firstVoteState.totalMember) {
          StepProgress(i <= firstVoteState.endedMember)
        }
      }
    }
    LunchVoteTextField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = firstVoteState.searchKeyword,
      hintText = stringResource(R.string.first_vote_hint_text),
      onTextChange = setSearchKeyword,
      textFieldType = TextFieldType.Search
    )
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      items(firstVoteState.foodMap.keys.filter { it.name.contains(firstVoteState.searchKeyword) }) {food ->
        FoodItem(food, firstVoteState.foodMap[food]!!) { onClickFood(food) }
      }
    }
    Button(
      onClick = navigateToSecondVote,
      modifier = Modifier.padding(bottom = 24.dp),
      enabled = firstVoteState.likeList.isNotEmpty() && firstVoteState.dislikeList.isNotEmpty()
    ) {
      Text("투표 완료")
    }
  }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FirstVoteScreenPreview() {
  LunchVoteTheme {
    Surface {
      FirstVoteScreen(
        FirstVoteState(
          foodMap = mapOf(
            FoodUIModel(
              id = "1",
              image = "",
              name = "음식명"
            ) to FoodStatus.DEFAULT,
            FoodUIModel(
              id = "2",
              image = "",
              name = "음식명"
            ) to FoodStatus.DEFAULT,
            FoodUIModel(
              id = "3",
              image = "",
              name = "음식명"
            ) to FoodStatus.DEFAULT,
          ),
          totalMember = 3,
          endedMember = 1
        )
      )
    }
  }
}