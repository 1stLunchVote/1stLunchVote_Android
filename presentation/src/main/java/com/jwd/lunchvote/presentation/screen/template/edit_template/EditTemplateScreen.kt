package com.jwd.lunchvote.presentation.screen.template.edit_template

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.FAB
import com.jwd.lunchvote.presentation.widget.FoodGrid
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TemplateTitle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditTemplateRoute(
  popBackStack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: EditTemplateViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it){
        is EditTemplateSideEffect.PopBackStack -> popBackStack()
        is EditTemplateSideEffect.OpenDeleteDialog -> viewModel.setDialogState(EditTemplateContract.DELETE_DIALOG)
        is EditTemplateSideEffect.OpenConfirmDialog -> viewModel.setDialogState(EditTemplateContract.CONFIRM_DIALOG)
        is EditTemplateSideEffect.CloseDialog -> viewModel.setDialogState("")
        is EditTemplateSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  when(dialog) {
    EditTemplateContract.CONFIRM_DIALOG -> ConfirmDialog(
      onDismissRequest = { viewModel.sendEvent(EditTemplateEvent.OnClickCancelButtonConfirmDialog) },
      onConfirmation = { viewModel.sendEvent(EditTemplateEvent.OnClickConfirmButtonConfirmDialog) }
    )
    EditTemplateContract.DELETE_DIALOG -> DeleteDialog(
      onDismissRequest = { viewModel.sendEvent(EditTemplateEvent.OnClickCancelButtonDeleteDialog) },
      onConfirmation = { viewModel.sendEvent(EditTemplateEvent.OnClickDeleteButtonDeleteDialog) }
    )
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(EditTemplateEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else EditTemplateScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun EditTemplateScreen(
  state: EditTemplateState,
  modifier: Modifier = Modifier,
  onEvent: (EditTemplateEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier
      .padding(horizontal = 24.dp)
      .padding(top = 8.dp),
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.edit_template_title),
        navIconVisible = true,
        popBackStack = { onEvent(EditTemplateEvent.OnClickBackButton) },
        actions = {
          IconButton(
            onClick = { onEvent(EditTemplateEvent.OnClickDeleteButton) }
          ) {
            Icon(
              Icons.Outlined.Delete,
              contentDescription = "delete"
            )
          }
        }
      )
    },
    actions = {
      if (state.foodItemList.any { it.status != FoodItem.Status.DEFAULT }) {
        FAB(
          text = stringResource(R.string.edit_template_save_button),
          onClick = { onEvent(EditTemplateEvent.OnClickSaveButton) }
        )
      }
    },
    scrollable = false
  ) {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
      val gridState = rememberLazyGridState()
      TemplateTitle(
        name = state.template.name,
        like = state.foodItemList.count { it.status == FoodItem.Status.LIKE },
        dislike = state.foodItemList.count { it.status == FoodItem.Status.DISLIKE },
        modifier = Modifier.fillMaxWidth(),
        gridState = gridState
      )
      FoodGrid(
        searchKeyword = state.searchKeyword,
        filteredFoodList = state.foodItemList.filter { it.food.name.contains(state.searchKeyword) },
        onSearchKeywordChange = { onEvent(EditTemplateEvent.OnSearchKeywordChange(it)) },
        onClickFoodItem = { onEvent(EditTemplateEvent.OnClickFoodItem(it)) },
        gridState = gridState,
        topPadding = 104.dp,
        bottomPadding = 104.dp
      )
    }
  }
}

@Composable
private fun ConfirmDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.edit_template_confirm_title),
    dismissText = stringResource(R.string.edit_template_confirm_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.edit_template_confirm_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Outlined.Edit,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    },
    content = {
      Text(
        stringResource(R.string.edit_template_confirm_content),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )
    }
  )
}

@Composable
private fun DeleteDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.edit_template_delete_title),
    dismissText = stringResource(R.string.edit_template_delete_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.edit_template_delete_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Outlined.Delete,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    },
    content = {
      Text(
        stringResource(R.string.edit_template_delete_content),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    EditTemplateScreen(
      state = EditTemplateState(
        foodItemList = List(10) {
          FoodItem(
            food = FoodUIModel(name = "${it}번째 음식")
          )
        },
        template = TemplateUIModel(
          name = "스트레스 받을 때(매운 음식)"
        )
      )
    )
  }
}

@Preview
@Composable
private fun ConfirmDialogPreview() {
  LunchVoteTheme {
    ConfirmDialog()
  }
}

@Preview
@Composable
private fun DeleteDialogPreview() {
  LunchVoteTheme {
    DeleteDialog()
  }
}