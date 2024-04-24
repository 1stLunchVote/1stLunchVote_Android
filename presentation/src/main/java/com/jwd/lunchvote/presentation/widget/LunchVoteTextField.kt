package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R

@Composable
fun LunchVoteTextField(
  text: String,
  onTextChange: (String) -> Unit,
  hintText: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  textFieldType: TextFieldType = TextFieldType.Default,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
  maxLines: Int = 1,
  focusManager: FocusManager = LocalFocusManager.current,
  keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
) {
  if (textFieldType == TextFieldType.Search) {
    OutlinedTextField(
      value = text,
      onValueChange = onTextChange,
      modifier = modifier,
      enabled = enabled,
      label = { Text(hintText) },
      leadingIcon = {
        Image(painterResource(R.drawable.ic_search), null)
      },
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedLabelColor = MaterialTheme.colorScheme.outlineVariant,
      ),
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions,
      keyboardActions = KeyboardActions(
        onDone = {
          if (keyboardOptions.imeAction == ImeAction.Next) {
            KeyboardActions.Default.onNext
          } else {
            KeyboardActions.Default.onDone
            focusManager.clearFocus()
            keyboardController?.hide()
          }
        }
      ),
      singleLine = maxLines == 1,
      maxLines = maxLines
    )
  } else {
    OutlinedTextField(
      value = text,
      onValueChange = onTextChange,
      modifier = modifier,
      enabled = enabled,
      label = { Text(hintText) },
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedLabelColor = MaterialTheme.colorScheme.outlineVariant,
      ),
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions,
      keyboardActions = KeyboardActions(
        onDone = {
          if (keyboardOptions.imeAction == ImeAction.Next) {
            KeyboardActions.Default.onNext
          } else {
            KeyboardActions.Default.onDone
            focusManager.clearFocus()
            keyboardController?.hide()
          }
        }
      ),
      singleLine = maxLines == 1,
      maxLines = maxLines
    )
  }
}

@Preview(showBackground = true)
@Composable
fun LunchVoteDefaultTextFieldPreview() {
  LunchVoteTheme {
    LunchVoteTextField(
      text = "",
      onTextChange = {},
      hintText = "hint"
    )
  }
}

@Preview(showBackground = true)
@Composable
fun LunchVoteSearchTextFieldPreview() {
  LunchVoteTheme {
    LunchVoteTextField(
      text = "",
      onTextChange = {},
      hintText = "hint",
      textFieldType = TextFieldType.Search
    )
  }
}

enum class TextFieldType {
  Default,
  Search
}