package com.jwd.lunchvote.presentation.screen.template.edit_template

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.DeleteDialogEvent
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.SaveDialogEvent
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.FAB
import com.jwd.lunchvote.presentation.widget.FoodGrid
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteModal
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

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it){
        is EditTemplateSideEffect.PopBackStack -> popBackStack()
        is EditTemplateSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(EditTemplateEvent.ScreenInitialize) }

  state.saveDialogState?.let {
    SaveDialog(onEvent = viewModel::sendEvent)
  }
  state.deleteDialogState?.let {
    DeleteDialog(onEvent = viewModel::sendEvent)
  }

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
              Icons.Rounded.Delete,
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
private fun SaveDialog(
  modifier: Modifier = Modifier,
  onEvent: (SaveDialogEvent) -> Unit = {}
) {
  LunchVoteModal(
    title = stringResource(R.string.et_save_dialog_title),
    onDismissRequest = { onEvent(SaveDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    iconColor = MaterialTheme.colorScheme.secondary,
    body = stringResource(R.string.et_save_dialog_body),
    closable = false,
    buttons = {
      DialogButton(
        text = stringResource(R.string.et_save_dialog_cancel_button),
        onClick = { onEvent(SaveDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.et_save_dialog_save_button),
        onClick = { onEvent(SaveDialogEvent.OnClickSaveButton) }
      )
    }
  )
}

@Composable
private fun DeleteDialog(
  modifier: Modifier = Modifier,
  onEvent: (DeleteDialogEvent) -> Unit = {}
) {
  LunchVoteModal(
    title = stringResource(R.string.et_delete_dialog_title),
    onDismissRequest = { onEvent(DeleteDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Delete,
        contentDescription = "Delete"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.et_delete_dialog_body),
    closable = false,
    buttons = {
      DialogButton(
        text = stringResource(R.string.c_delete_dialog_cancel_button),
        onClick = { onEvent(DeleteDialogEvent.OnClickCancelButton) },
        isDismiss = true,
        color = MaterialTheme.colorScheme.onSurface
      )
      DialogButton(
        text = stringResource(R.string.c_delete_dialog_delete_button),
        onClick = { onEvent(DeleteDialogEvent.OnClickDeleteButton) },
        color = MaterialTheme.colorScheme.error
      )
    }
  )
}

@Preview
@Composable
private fun Default() {
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
private fun SaveDialogPreview() {
  LunchVoteTheme {
    SaveDialog()
  }
}

@Preview
@Composable
private fun DeleteDialogPreview() {
  LunchVoteTheme {
    DeleteDialog()
  }
}