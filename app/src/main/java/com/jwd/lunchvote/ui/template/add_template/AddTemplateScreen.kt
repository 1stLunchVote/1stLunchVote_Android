package com.jwd.lunchvote.ui.template.add_template

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.widget.FoodItem
import com.jwd.lunchvote.widget.LikeDislike
import com.jwd.lunchvote.widget.LunchVoteTextField
import com.jwd.lunchvote.widget.LunchVoteTopBar
import com.jwd.lunchvote.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTemplateRoute(
  popBackStack: (String) -> Unit,
  viewModel: AddTemplateViewModel = hiltViewModel()
){
  val addTemplateState : AddTemplateState by viewModel.viewState.collectAsStateWithLifecycle()

  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is AddTemplateSideEffect.PopBackStack -> popBackStack(it.message)
        is AddTemplateSideEffect.ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
      }
    }
  }

  AddTemplateScreen(
    addTemplateState = addTemplateState,
    snackBarHostState = snackBarHostState,
    onClickBackButton = { viewModel.sendEvent(AddTemplateEvent.OnClickBackButton) },
    onClickFood = { food -> viewModel.sendEvent(AddTemplateEvent.OnClickFood(food)) },
    setSearchKeyword = { searchKeyword -> viewModel.sendEvent(AddTemplateEvent.SetSearchKeyword(searchKeyword)) },
    onClickAddButton = { viewModel.sendEvent(AddTemplateEvent.OnClickAddButton) }
  )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AddTemplateScreen(
  addTemplateState: AddTemplateState,
  snackBarHostState: SnackbarHostState,
  onClickBackButton: () -> Unit = {},
  onClickFood: (FoodUIModel) -> Unit = {},
  setSearchKeyword: (String) -> Unit = {},
  onClickAddButton: () -> Unit = {}
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
  ) { padding ->
    if (addTemplateState.loading) {
      Dialog(onDismissRequest = {  }) { CircularProgressIndicator() }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        horizontalAlignment = CenterHorizontally
      ) {
        LunchVoteTopBar(
          title = "템플릿 생성",
          navIconVisible = true,
          popBackStack = onClickBackButton
        )
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
          horizontalAlignment = CenterHorizontally
        ) {
          TemplateTitle(
            addTemplateState.name,
            addTemplateState.likeList.size,
            addTemplateState.dislikeList.size
          )
          LunchVoteTextField(
            modifier = Modifier.fillMaxWidth(),
            text = addTemplateState.searchKeyword,
            hintText = stringResource(R.string.first_vote_hint_text),
            onTextChanged = setSearchKeyword,
            textFieldType = TextFieldType.Search
          )
          LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            items(addTemplateState.foodList.filter { it.name.contains(addTemplateState.searchKeyword) }) {food ->
              FoodItem(food) { onClickFood(food) }
            }
          }
          Button(
            onClick = onClickAddButton,
            enabled = addTemplateState.likeList.isNotEmpty() || addTemplateState.dislikeList.isNotEmpty()
          ) {
            Text("투표 완료")
          }
        }
      }
    }
  }
}

@Composable
fun TemplateTitle(
  name: String,
  like: Int,
  dislike: Int
) {
  val shape = RoundedCornerShape(8.dp)
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape)
      .background(MaterialTheme.colorScheme.background, shape)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), shape)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = CenterHorizontally
    ) {
      Text(name, style = MaterialTheme.typography.bodyLarge)
      LikeDislike(like, dislike)
    }
  }
}

@Preview(showSystemUi = true)
@Composable
fun AddTemplateScreenPreview() {
  LunchVoteTheme {
    Surface {
      AddTemplateScreen(
        AddTemplateState(
          name = "학생회 회식 대표 메뉴",
          foodList = List(20) {
            FoodUIModel(
              id = "$it",
              imageUrl = "",
              name = "음식명",
              status = FoodStatus.DEFAULT
            )
          }
        ),
        snackBarHostState = remember { SnackbarHostState() }
      )
    }
  }
}