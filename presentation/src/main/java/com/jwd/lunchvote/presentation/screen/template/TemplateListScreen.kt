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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.AddDialogEvent
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextField
import com.jwd.lunchvote.presentation.widget.TopBar
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

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is TemplateListSideEffect.PopBackStack -> popBackStack()
        is TemplateListSideEffect.NavigateToAddTemplate -> navigateToAddTemplate(it.templateName)
        is TemplateListSideEffect.NavigateToEditTemplate -> navigateToEditTemplate(it.templateId)
        is TemplateListSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(TemplateListEvent.ScreenInitialize) }

  state.addDialogState?.let { dialogState ->
    AddDialog(
      templateName = dialogState.templateName,
      modifier = modifier,
      onEvent = viewModel::sendEvent
    )
  }

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
      TopBar(
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
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.medium)
      .clickable { onClick() }
      .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
      .border(2.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
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
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.small)
      .clickable { onClick() }
      .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.shapes.small)
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
  onEvent: (AddDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.tl_add_dialog_title),
    onDismissRequest = { onEvent(AddDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Image(
        imageVector = Icons.Rounded.Add,
        contentDescription = "Add"
      )
    },
    iconColor = MaterialTheme.colorScheme.secondary,
    body = stringResource(R.string.tl_add_dialog_body),
    closable = true,
    content = {
      TextField(
        text = templateName,
        hintText = stringResource(R.string.tl_add_dialog_hint_text),
        onTextChange = { onEvent(AddDialogEvent.OnTemplateNameChange(it)) },
      )
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.tl_add_dialog_cancel_button),
        onClick = { onEvent(AddDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.tl_add_dialog_add_button),
        onClick = { onEvent(AddDialogEvent.OnClickAddButton) },
        enabled = templateName.isNotBlank()
      )
    }
  )
}

@Preview
@Composable
private fun Default() {
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
private fun NoTemplate() {
  ScreenPreview {
    TemplateListScreen(
      TemplateListState()
    )
  }
}

@Preview
@Composable
private fun AddDialogPreview() {
  LunchVoteTheme {
    AddDialog(
      templateName = "템플릿 이름"
    )
  }
}