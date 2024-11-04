package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect

@Composable
fun LunchVoteTextField(
  text: String,
  onTextChange: (String) -> Unit,
  hintText: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
  maxLines: Int = 1,
  focusManager: FocusManager = LocalFocusManager.current,
  keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
) {
  OutlinedTextField(
    value = text,
    onValueChange = onTextChange,
    modifier = modifier,
    enabled = enabled,
    label = { Text(hintText) },
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    colors = OutlinedTextFieldDefaults.colors(
      unfocusedLabelColor = MaterialTheme.colorScheme.outlineVariant,
    ),
    isError = isError,
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

@Composable
fun SearchIcon(
  modifier: Modifier = Modifier
) {
  Icon(
    painter = painterResource(R.drawable.ic_search),
    contentDescription = "search",
    modifier = modifier
  )
}

@Composable
fun PasswordVisibleIcon(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Icon(
    painter = painterResource(R.drawable.ic_password_visible),
    contentDescription = "password visible",
    modifier = modifier.clickableWithoutEffect(onClick)
  )
}

@Composable
fun PasswordInvisibleIcon(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Icon(
    painter = painterResource(R.drawable.ic_password_invisible),
    contentDescription = "password invisible",
    modifier = modifier.clickableWithoutEffect(onClick)
  )
}

@Preview(showBackground = true)
@Composable
private fun DefaultTextFieldPreview() {
  LunchVoteTheme {
    LunchVoteTextField(
      text = "", onTextChange = {}, hintText = "hint"
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun SearchTextFieldPreview() {
  LunchVoteTheme {
    LunchVoteTextField(text = "", onTextChange = {}, hintText = "hint", leadingIcon = {
      SearchIcon()
    })
  }
}

@Preview(showBackground = true)
@Composable
private fun PasswordTextFieldPreview() {
  LunchVoteTheme {
    LunchVoteTextField(text = "", onTextChange = {}, hintText = "hint", trailingIcon = {
      PasswordVisibleIcon({})
    }, visualTransformation = PasswordVisualTransformation()
    )
  }
}