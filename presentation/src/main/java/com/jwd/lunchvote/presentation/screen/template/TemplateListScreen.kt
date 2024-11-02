package com.jwd.lunchvote.presentation.screen.template

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TemplateListRoute(
  popBackStack: () -> Unit,
  navigateToAddTemplate: (String) -> Unit,
  navigateToEditTemplate: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: TemplateListViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is TemplateListSideEffect.PopBackStack -> popBackStack()
        is TemplateListSideEffect.NavigateToAddTemplate -> navigateToAddTemplate(it.templateName)
        is TemplateListSideEffect.NavigateToEditTemplate -> navigateToEditTemplate(it.templateId)
        is TemplateListSideEffect.OpenAddDialog -> viewModel.setDialogState(TemplateListContract.ADD_DIALOG)
        is TemplateListSideEffect.CloseDialog -> viewModel.setDialogState("")
        is TemplateListSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  when(dialog) {
    TemplateListContract.ADD_DIALOG -> AddDialog(
      templateName = state.templateName ?: "",
      modifier = modifier,
      onTemplateNameChange = { viewModel.sendEvent(TemplateListEvent.OnTemplateNameChange(it)) },
      onDismissRequest = { viewModel.sendEvent(TemplateListEvent.OnClickDismissButtonAddDialog) },
      onConfirmation = { viewModel.sendEvent(TemplateListEvent.OnClickConfirmButtonAddDialog) }
    )
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(TemplateListEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else TemplateListScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun TemplateListScreen(
  state: TemplateListState,
  modifier: Modifier = Modifier,
  onEvent: (TemplateListEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.template_list_title),
        navIconVisible = true,
        popBackStack = { onEvent(TemplateListEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, start = 24.dp, end = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      if (state.templateList.isEmpty()) {
        item {
          Text(
            text = stringResource(R.string.template_list_no_template),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
          )
        }
      } else {
        items(state.templateList) { template ->
          TemplateListItem(
            template = template,
            onClick = { onEvent(TemplateListEvent.OnClickTemplate(template.id)) }
          )
        }
      }
      item {
        TemplateListButton(
          onClick = { onEvent(TemplateListEvent.OnClickAddButton) }
        )
      }
    }
  }
}

@Composable
private fun TemplateListItem(
  template: TemplateUIModel,
  onClick: () -> Unit,
) {
  val shape = RoundedCornerShape(8.dp)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape)
      .clickable { onClick() }
      .background(MaterialTheme.colorScheme.background, shape)
      .border(2.dp, MaterialTheme.colorScheme.outlineVariant, shape)
      .padding(horizontal = 16.dp, vertical = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = template.name,
        style = MaterialTheme.typography.bodyLarge
      )
      LikeDislike(
        like = template.likedFoodIds.size,
        dislike = template.dislikedFoodIds.size
      )
    }
    Image(
      painterResource(R.drawable.ic_caret_right),
      contentDescription = null
    )
  }
}

@Composable
private fun TemplateListButton(
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(8.dp)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape)
      .clickable { onClick() }
      .background(MaterialTheme.colorScheme.background, shape)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), shape)
      .padding(vertical = 20.dp),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painterResource(R.drawable.ic_add),
      contentDescription = null
    )
  }
}

@Composable
private fun AddDialog(
  templateName: String,
  modifier: Modifier = Modifier,
  onTemplateNameChange: (String) -> Unit = {},
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.template_list_add_dialog_title),
    dismissText = stringResource(R.string.template_list_add_dialog_dismiss_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.template_list_add_dialog_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    confirmEnabled = templateName.isNotBlank()
  ) {
    LunchVoteTextField(
      text = templateName,
      hintText = stringResource(R.string.template_list_add_dialog_hint_text),
      onTextChange = onTemplateNameChange,
    )
  }
}

@Preview
@Composable
private fun Preview1() {
  ScreenPreview {
    TemplateListScreen(
      TemplateListState()
    )
  }
}

@Preview
@Composable
private fun Preview2() {
  ScreenPreview {
    TemplateListScreen(
      TemplateListState(
        templateList = listOf(
          TemplateUIModel(
            id = "1",
            name = "템플릿 1",
            likedFoodIds = listOf("1", "2"),
            dislikedFoodIds = listOf("3", "4")
          ),
          TemplateUIModel(
            id = "2",
            name = "템플릿 2",
            likedFoodIds = listOf("1", "2"),
            dislikedFoodIds = listOf("3", "4")
          )
        )
      )
    )
  }
}

@Preview
@Composable
private fun AddDialogPreview() {
  com.jwd.lunchvote.theme.LunchVoteTheme {
    AddDialog(
      templateName = "템플릿 이름"
    )
  }
}