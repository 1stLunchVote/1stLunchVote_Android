package com.jwd.lunchvote.presentation.ui.template

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.TemplateListButton
import com.jwd.lunchvote.presentation.widget.TemplateListItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TemplateListRoute(
  navigateToEditTemplate: (String?) -> Unit,
  openAddDialog: () -> Unit,
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: TemplateListViewModel = hiltViewModel(),
  context: Context = LocalContext.current
){
  val templateListState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is TemplateListSideEffect.PopBackStack -> popBackStack()
        is TemplateListSideEffect.NavigateToEditTemplate -> navigateToEditTemplate(it.templateId)
        is TemplateListSideEffect.OpenAddDialog -> openAddDialog()
        is TemplateListSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else TemplateListScreen(
    templateListState = templateListState,
    modifier = modifier,
    onClickBackButton = { viewModel.sendEvent(TemplateListEvent.OnClickBackButton) },
    onClickTemplate = { templateId -> viewModel.sendEvent(
      TemplateListEvent.OnClickTemplate(
        templateId
      )
    ) },
    onClickAddButton = { viewModel.sendEvent(TemplateListEvent.OnClickAddButton) }
  )
}

@Composable
private fun TemplateListScreen(
  templateListState: TemplateListState,
  modifier: Modifier = Modifier,
  onClickBackButton: () -> Unit = {},
  onClickTemplate: (String) -> Unit = {},
  onClickAddButton: () -> Unit = {},
) {
  Column(
    modifier = modifier.fillMaxSize(),
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

@Preview(showSystemUi = true)
@Composable
fun TemplateListScreenPreview() {
  LunchVoteTheme {
    TemplateListScreen(
      TemplateListState()
    )
  }
}