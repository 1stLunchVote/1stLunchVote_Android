package com.jwd.lunchvote.ui.template.edit_template

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.template.add_template.TemplateTitle
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.*
import com.jwd.lunchvote.widget.FoodItem
import com.jwd.lunchvote.widget.LunchVoteTextField
import com.jwd.lunchvote.widget.LunchVoteTopBar
import com.jwd.lunchvote.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditTemplateRoute(
  popBackStack: (String) -> Unit,
  viewModel: EditTemplateViewModel = hiltViewModel()
){
  val editTemplateState : EditTemplateState by viewModel.viewState.collectAsStateWithLifecycle()
  val editTemplateDialogState : EditTemplateDialogState? by viewModel.dialogState.collectAsStateWithLifecycle()

  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is EditTemplateSideEffect.PopBackStack -> popBackStack(it.message)
        is EditTemplateSideEffect.ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
      }
    }
  }

  EditTemplateDialog(
    editTemplateDialogState = editTemplateDialogState,
    onClickDismissButton = { viewModel.sendEvent(EditTemplateEvent.OnClickDialogDismiss) }
  )

  EditTemplateScreen(
    editTemplateState = editTemplateState,
    snackBarHostState = snackBarHostState,
    onClickBackButton = { viewModel.sendEvent(EditTemplateEvent.OnClickBackButton) },
    setSearchKeyword = { viewModel.sendEvent(EditTemplateEvent.SetSearchKeyword(it)) },
    onClickFood = { viewModel.sendEvent(EditTemplateEvent.OnClickFood(it)) },
    onClickSaveButton = { viewModel.sendEvent(EditTemplateEvent.OnClickSaveButton) },
    onClickDeleteButton = { viewModel.sendEvent(EditTemplateEvent.OnClickDeleteButton) }
  )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EditTemplateScreen(
  editTemplateState: EditTemplateState,
  snackBarHostState: SnackbarHostState,
  onClickBackButton: () -> Unit = {},
  setSearchKeyword: (String) -> Unit = {},
  onClickFood: (FoodUIModel) -> Unit = {},
  onClickSaveButton: () -> Unit = {},
  onClickDeleteButton: () -> Unit = {}
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
  ) { padding ->
    if (editTemplateState.loading) {
      Dialog(onDismissRequest = {  }) { CircularProgressIndicator() }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
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
            items(editTemplateState.foodList.filter { it.name.contains(editTemplateState.searchKeyword) }) {food ->
              FoodItem(food) { onClickFood(food) }
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
  }
}

@Preview(showSystemUi = true)
@Composable
fun EditTemplateScreenPreview() {
  LunchVoteTheme {
    EditTemplateScreen(
      editTemplateState = EditTemplateState(
        foodList = List(20) {
          FoodUIModel(
            id = "$it",
            imageUrl = "",
            name = "음식명",
            status = FoodStatus.DEFAULT
          )
        },
        template = TemplateUIModel(
          Template(
            name = "스트레스 받을 때(매운 음식)"
          )
        )
      ),
      snackBarHostState = remember { SnackbarHostState() }
    )
  }
}