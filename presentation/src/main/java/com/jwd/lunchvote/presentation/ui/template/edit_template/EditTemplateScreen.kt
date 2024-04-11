package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import com.jwd.lunchvote.presentation.ui.template.add_template.TemplateTitle
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditTemplateRoute(
  openDeleteDialog: () -> Unit,
  openConfirmDialog: () -> Unit,
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: EditTemplateViewModel = hiltViewModel(),
  context: Context = LocalContext.current
){
  val editTemplateState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is EditTemplateSideEffect.OpenDeleteDialog -> openDeleteDialog()
        is EditTemplateSideEffect.OpenConfirmDialog -> openConfirmDialog()
        is EditTemplateSideEffect.PopBackStack -> popBackStack()
        is EditTemplateSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else EditTemplateScreen(
    editTemplateState = editTemplateState,
    modifier = modifier,
    onClickBackButton = { viewModel.sendEvent(EditTemplateEvent.OnClickBackButton) },
    setSearchKeyword = { viewModel.sendEvent(EditTemplateEvent.SetSearchKeyword(it)) },
    onClickFood = { viewModel.sendEvent(EditTemplateEvent.OnClickFood(it)) },
    onClickSaveButton = { viewModel.sendEvent(EditTemplateEvent.OnClickSaveButton) },
    onClickDeleteButton = { viewModel.sendEvent(EditTemplateEvent.OnClickDeleteButton) }
  )
}

@Composable
private fun EditTemplateScreen(
  editTemplateState: EditTemplateState,
  modifier: Modifier = Modifier,
  onClickBackButton: () -> Unit = {},
  setSearchKeyword: (String) -> Unit = {},
  onClickFood: (FoodUIModel) -> Unit = {},
  onClickSaveButton: () -> Unit = {},
  onClickDeleteButton: () -> Unit = {}
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = CenterHorizontally
  ) {
    LunchVoteTopBar(
      title = "템플릿 편집",
      navIconVisible = true,
      popBackStack = onClickBackButton,
      actions = {
        IconButton(onClickDeleteButton) {
          Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "delete",
          )
        }
      }
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = CenterHorizontally
    ) {
      TemplateTitle(
        editTemplateState.template.name,
        editTemplateState.likeList.size,
        editTemplateState.dislikeList.size
      )
      LunchVoteTextField(
        modifier = Modifier.fillMaxWidth(),
        text = editTemplateState.searchKeyword,
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
        items(editTemplateState.foodMap.keys.filter { it.name.contains(editTemplateState.searchKeyword) }) {food ->
          FoodItem(food, editTemplateState.foodMap[food]!!) { onClickFood(food) }
        }
      }
      Button(
        onClick = onClickSaveButton,
        enabled = editTemplateState.likeList.isNotEmpty() || editTemplateState.dislikeList.isNotEmpty()
      ) {
        Text("템플릿 수정")
      }
    }
  }
}

@Preview(showSystemUi = true)
@Composable
fun EditTemplateScreenPreview() {
  LunchVoteTheme {
    EditTemplateScreen(
      editTemplateState = EditTemplateState(
        foodMap = mapOf(
          FoodUIModel(
            id = "1",
            imageUrl = "",
            name = "음식명"
          ) to FoodStatus.DEFAULT,
          FoodUIModel(
            id = "2",
            imageUrl = "",
            name = "음식명"
          ) to FoodStatus.DEFAULT,
          FoodUIModel(
            id = "3",
            imageUrl = "",
            name = "음식명"
          ) to FoodStatus.DEFAULT,
        ),
        template = TemplateUIModel(
          Template(
            name = "스트레스 받을 때(매운 음식)"
          )
        )
      )
    )
  }
}