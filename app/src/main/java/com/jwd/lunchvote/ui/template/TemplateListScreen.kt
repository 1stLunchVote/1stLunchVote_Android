package com.jwd.lunchvote.ui.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.navigation.SNACK_BAR_KEY
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListDialogState
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickAddButton
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickBackButton
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickDismissButton
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.StartInitialize
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.NavigateToAddTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.NavigateToEditTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.PopBackStack
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.ShowSnackBar
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.widget.LunchVoteTopBar
import com.jwd.lunchvote.widget.TemplateListButton
import com.jwd.lunchvote.widget.TemplateListItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TemplateListRoute(
  navigateToEditTemplate: (String?) -> Unit,
  navigateToAddTemplate: (String) -> Unit,
  popBackStack: () -> Unit,
  savedStateHandle: SavedStateHandle,
  viewModel: TemplateListViewModel = hiltViewModel()
){
  val templateListState : TemplateListState by viewModel.viewState.collectAsStateWithLifecycle()
  val templateListDialogState : TemplateListDialogState? by viewModel.dialogState.collectAsStateWithLifecycle()

  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is PopBackStack -> popBackStack()
        is NavigateToEditTemplate -> navigateToEditTemplate(it.templateId)
        is NavigateToAddTemplate -> navigateToAddTemplate(it.templateName)
        is ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
      }
    }
  }

  LaunchedEffect(Unit){
    viewModel.sendEvent(StartInitialize)

    val message = savedStateHandle.get<String>(SNACK_BAR_KEY)
    if (!message.isNullOrEmpty()) {
      savedStateHandle[SNACK_BAR_KEY] = ""
      snackBarHostState.showSnackbar(message)
    }
  }

  TemplateListDialog(
    templateListDialogState = templateListDialogState,
    onClickDismissButton = { viewModel.sendEvent(OnClickDismissButton) }
  )

  TemplateListScreen(
    templateListState = templateListState,
    snackBarHostState = snackBarHostState,
    onClickBackButton = { viewModel.sendEvent(OnClickBackButton) },
    onClickTemplate = { templateId -> viewModel.sendEvent(OnClickTemplate(templateId)) },
    onClickAddButton = { viewModel.sendEvent(OnClickAddButton) }
  )
}

@Composable
private fun TemplateListScreen(
  templateListState: TemplateListState,
  snackBarHostState: SnackbarHostState,
  onClickBackButton: () -> Unit = {},
  onClickTemplate: (String) -> Unit = {},
  onClickAddButton: () -> Unit = {},
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
  ) { padding ->
    if (templateListState.loading) {
      Dialog(onDismissRequest = {  }) { CircularProgressIndicator() }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        horizontalAlignment = CenterHorizontally
      ) {
        LunchVoteTopBar(
          title = "템플릿 목록",
          navIconVisible = true,
          popBackStack = onClickBackButton
        )
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          if (templateListState.templateList.isEmpty()) {
            item {
              Text(
                "템플릿이 사전 설정되어 있지 않습니다.\n하단의 [+] 버튼을 눌러 템플릿을 추가해주세요.",
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 20.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
              )
            }
          } else {
            items(templateListState.templateList) { template ->
              TemplateListItem(template) {
                onClickTemplate(template.id)
              }
            }
          }
          item { TemplateListButton(onClickAddButton) }
        }
      }
    }
  }
}

@Preview(showSystemUi = true)
@Composable
fun TemplateListScreenPreview() {
  LunchVoteTheme {
    TemplateListScreen(
      templateListState = TemplateListState(),
      snackBarHostState = remember { SnackbarHostState() }
    )
  }
}