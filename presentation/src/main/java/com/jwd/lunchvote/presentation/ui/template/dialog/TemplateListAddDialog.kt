package com.jwd.lunchvote.presentation.ui.template.dialog

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddContract.TemplateListAddEvent
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddContract.TemplateListAddSideEffect
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TemplateListAddDialog(
  modifier: Modifier = Modifier,
  popBackStack: () -> Unit = {},
  navigateToAddTemplate: (String) -> Unit = {},
  showSnackBar: suspend (String) -> Unit = {},
  viewModel: TemplateListAddViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val templateListAddState by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is TemplateListAddSideEffect.PopBackStack -> popBackStack()
        is TemplateListAddSideEffect.NavigateToAddTemplate -> navigateToAddTemplate(it.templateName)
        is TemplateListAddSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  LunchVoteDialog(
    title = stringResource(R.string.template_list_add_dialog_title),
    dismissText = stringResource(R.string.template_list_add_dialog_dismiss_button),
    onDismiss = { viewModel.sendEvent(TemplateListAddEvent.OnClickDismissButton) },
    confirmText = stringResource(R.string.template_list_add_dialog_confirm_button),
    onConfirm = { viewModel.sendEvent(TemplateListAddEvent.OnClickConfirmButton) },
    modifier = modifier,
    confirmEnabled = templateListAddState.templateName.isNotBlank()
  ) {
    LunchVoteTextField(
      text = templateListAddState.templateName,
      hintText = stringResource(R.string.template_list_add_dialog_hint_text),
      onTextChange = { viewModel.sendEvent(TemplateListAddEvent.OnTemplateNameChange(it)) },
    )
  }
}

@Preview
@Composable
private fun TemplateListAddDialogPreview() {
  LunchVoteTheme {
    TemplateListAddDialog()
  }
}