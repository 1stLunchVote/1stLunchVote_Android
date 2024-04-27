package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.widget.FoodItem
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextFieldType
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun EditTemplateRoute(
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: EditTemplateViewModel = hiltViewModel(),
  context: Context = LocalContext.current
){
  val editTemplateState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it){
        is EditTemplateSideEffect.PopBackStack -> popBackStack()
        is EditTemplateSideEffect.OpenDeleteDialog -> viewModel.setDialogState(EditTemplateContract.DELETE_DIALOG)
        is EditTemplateSideEffect.OpenConfirmDialog -> viewModel.setDialogState(EditTemplateContract.CONFIRM_DIALOG)
        is EditTemplateSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  when(dialogState) {
    EditTemplateContract.CONFIRM_DIALOG -> {
      EditTemplateConfirmDialog(
        onDismissRequest = { viewModel.sendEvent(EditTemplateEvent.OnClickCancelButtonConfirmDialog) },
        onConfirmation = { viewModel.sendEvent(EditTemplateEvent.OnClickConfirmButtonConfirmDialog) }
      )
    }
    EditTemplateContract.DELETE_DIALOG -> {
      EditTemplateDeleteDialog(
        onDismissRequest = { viewModel.sendEvent(EditTemplateEvent.OnClickCancelButtonDeleteDialog) },
        onConfirmation = { viewModel.sendEvent(EditTemplateEvent.OnClickDeleteButtonDeleteDialog) }
      )
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
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.edit_template_title),
        navIconVisible = true,
        popBackStack = onClickBackButton,
        actions = {
          IconButton(onClickDeleteButton) {
            Icon(Icons.Outlined.Delete, "delete")
          }
        }
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      TemplateTitle(
        name = editTemplateState.template.name,
        like = editTemplateState.likeList.size,
        dislike = editTemplateState.dislikeList.size,
        modifier = Modifier.fillMaxWidth()
      )
      LunchVoteTextField(
        text = editTemplateState.searchKeyword,
        onTextChange = setSearchKeyword,
        hintText = stringResource(R.string.edit_template_hint_text),
        modifier = Modifier.fillMaxWidth(),
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
        val filteredFoodList = editTemplateState.foodMap.keys.filter { it.name.contains(editTemplateState.searchKeyword) }

        items(filteredFoodList) {food ->
          FoodItem(
            food = food,
            status = editTemplateState.foodMap[food] ?: FoodStatus.DEFAULT
          ) { onClickFood(food) }
        }
      }
      Button(
        onClick = onClickSaveButton,
        modifier = Modifier.align(CenterHorizontally),
        enabled = editTemplateState.likeList.isNotEmpty() || editTemplateState.dislikeList.isNotEmpty()
      ) {
        Text(stringResource(R.string.edit_template_save_button))
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
  val shape = RoundedCornerShape(8.dp)

  Column(
    modifier = modifier
      .clip(shape)
      .background(MaterialTheme.colorScheme.background, shape)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), shape)
      .padding(vertical = 20.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = CenterHorizontally
  ) {
    Text(
      name,
      style = MaterialTheme.typography.bodyLarge
    )
    LikeDislike(like, dislike)
  }
}

@Preview
@Composable
private fun EditTemplateScreenPreview() {
  ScreenPreview {
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
          name = "스트레스 받을 때(매운 음식)"
        )
      )
    )
  }
}