package com.jwd.lunchvote.ui.template.add_template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.widget.LunchVoteTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTemplateRoute(
  popBackStack: (String) -> Unit,
  viewModel: AddTemplateViewModel = hiltViewModel()
){
  val addTemplateState : AddTemplateState by viewModel.viewState.collectAsStateWithLifecycle()

  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is AddTemplateSideEffect.PopBackStack -> popBackStack(it.message)
        is AddTemplateSideEffect.ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
      }
    }
  }

  AddTemplateScreen(
    addTemplateState = addTemplateState,
    snackBarHostState = snackBarHostState,
    onClickBackButton = { viewModel.sendEvent(AddTemplateEvent.OnClickBackButton) }
  )
}

@Composable
private fun AddTemplateScreen(
  addTemplateState: AddTemplateState,
  snackBarHostState: SnackbarHostState,
  onClickBackButton: () -> Unit = {}
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
  ) { padding ->
    if (addTemplateState.loading) {
      Dialog(onDismissRequest = {  }) { CircularProgressIndicator() }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        horizontalAlignment = CenterHorizontally
      ) {
        LunchVoteTopBar(
          title = "템플릿 생성",
          navIconVisible = true,
          popBackStack = onClickBackButton
        )

      }
    }
  }
}

@Preview(showSystemUi = true)
@Composable
fun AddTemplateScreenPreview() {
  LunchVoteTheme {
    AddTemplateScreen(
      addTemplateState = AddTemplateState(),
      snackBarHostState = remember { SnackbarHostState() }
    )
  }
}