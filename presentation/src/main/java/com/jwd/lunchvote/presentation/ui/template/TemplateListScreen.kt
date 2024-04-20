package com.jwd.lunchvote.presentation.ui.template

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.widget.LikeDislike
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TemplateListRoute(
  popBackStack: () -> Unit,
  navigateToEditTemplate: (String?) -> Unit,
  openAddDialog: () -> Unit,
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
    onClickTemplate = { viewModel.sendEvent(TemplateListEvent.OnClickTemplate(it)) },
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
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "템플릿 목록",
        navIconVisible = true,
        popBackStack = onClickBackButton
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
          TemplateListItem(
            template = template,
            onClick = { onClickTemplate(template.id) }
          )
        }
      }
      item { TemplateListButton(onClickAddButton) }
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
        template.name,
        style = MaterialTheme.typography.bodyLarge
      )
      LikeDislike(
        like = template.like.size,
        dislike = template.dislike.size
      )
    }
    Image(
      painterResource(R.drawable.ic_caret_right),
      null
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
      null
    )
  }
}

@Preview
@Composable
private fun TemplateListScreenPreview1() {
  ScreenPreview {
    TemplateListScreen(
      TemplateListState()
    )
  }
}

@Preview
@Composable
private fun TemplateListScreenPreview2() {
  ScreenPreview {
    TemplateListScreen(
      TemplateListState(
        templateList = listOf(
          TemplateUIModel(
            id = "1",
            name = "템플릿 1",
            like = listOf("1", "2"),
            dislike = listOf("3", "4")
          ),
          TemplateUIModel(
            id = "2",
            name = "템플릿 2",
            like = listOf("1", "2"),
            dislike = listOf("3", "4")
          )
        )
      )
    )
  }
}