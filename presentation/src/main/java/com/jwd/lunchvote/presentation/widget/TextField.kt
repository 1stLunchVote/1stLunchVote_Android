package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.util.conditional
import com.jwd.lunchvote.presentation.util.innerShadow
import com.jwd.lunchvote.presentation.util.outerShadow

@Composable
fun TextField(
  text: String,
  onTextChange: (String) -> Unit,
  hintText: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  isError: Boolean? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  singleLine: Boolean = true,
  keyboardOptions: KeyboardOptions = if (singleLine) KeyboardOptions(imeAction = ImeAction.Done) else KeyboardOptions.Default,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  focusManager: FocusManager = LocalFocusManager.current,
  keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
  isFocused: Boolean = false
) {
  var focused by remember { mutableStateOf(isFocused) }

  val color = when (isError) {
    null -> MaterialTheme.colorScheme.primary
    false -> MaterialTheme.colorScheme.secondary
    true -> MaterialTheme.colorScheme.error
  }

  BasicTextField(
    value = text,
    onValueChange = onTextChange,
    modifier = modifier.onFocusChanged { focused = it.isFocused },
    enabled = enabled,
    readOnly = readOnly,
    textStyle = MaterialTheme.typography.bodyMedium,
    visualTransformation = visualTransformation,
    singleLine = singleLine,
    maxLines = maxLines,
    keyboardOptions = keyboardOptions,
    keyboardActions = KeyboardActions(
      onDone = {
        when (keyboardOptions.imeAction) {
          ImeAction.Next -> {
            KeyboardActions.Default.onNext
          }
          ImeAction.Done -> {
            KeyboardActions.Default.onDone
            focusManager.clearFocus()
            keyboardController?.hide()
          }
          else -> {
            KeyboardOptions.Default
          }
        }
      }
    )
  ) { innerTextField ->
    Row(
      modifier = modifier
        .conditional(focused, modifierIf = {
          outerShadow(
            color = color,
            shape = MaterialTheme.shapes.small,
            offsetY = 0.dp,
            blur = 4.dp,
          )
        }, modifierElse = {
          innerShadow(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.32f),
            shape = MaterialTheme.shapes.small,
            offsetY = 2.dp,
            blur = 2.dp
          )
        })
        .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
        .conditional(text.isNotEmpty() || focused, modifierIf = {
          border(1.dp, color, MaterialTheme.shapes.small)
        }, modifierElse = {
          background(MaterialTheme.colorScheme.onBackground.copy(0.1f), MaterialTheme.shapes.small)
        })
        .conditional(enabled.not()) {
          alpha(0.32f)
        }
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.Top
    ) {
      leadingIcon?.invoke()
      Box(
        modifier = Modifier
          .weight(1f)
          .heightIn(min = 20.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        innerTextField()
        if (hintText.isNotEmpty() && text.isEmpty()) {
          Text(
            text = hintText,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
      if (isError == true) {
        ErrorIcon()
      }
      trailingIcon?.invoke()
    }
  }
}

@Composable
fun TextField(
  text: String,
  onTextChange: (String) -> Unit,
  hintText: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  isError: Boolean? = null,
  errorMessage: String? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
  singleLine: Boolean = true,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  focusManager: FocusManager = LocalFocusManager.current,
  keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
  isFocused: Boolean = false
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    TextField(
      text = text,
      onTextChange = onTextChange,
      hintText = hintText,
      enabled = enabled,
      readOnly = readOnly,
      isError = isError,
      leadingIcon = leadingIcon,
      trailingIcon = trailingIcon,
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions,
      maxLines = maxLines,
      focusManager = focusManager,
      keyboardController = keyboardController,
      isFocused = isFocused
    )
    errorMessage?.let { message ->
      Text(
        text = message,
        modifier = Modifier
          .padding(horizontal = 8.dp)
          .conditional(isError == null || isError == false) { alpha(0f) },
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelMedium
      )
    }
  }
}

@Composable
fun PasswordField(
  text: String,
  onTextChange: (String) -> Unit,
  hintText: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  isError: Boolean? = null,
  errorMessage: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
  focusManager: FocusManager = LocalFocusManager.current,
  keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
  isFocused: Boolean = false,
  isVisible: Boolean = false
) {
  var visible by remember { mutableStateOf(isVisible) }

  TextField(
    text = text,
    onTextChange = onTextChange,
    hintText = hintText,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    isError = isError,
    errorMessage = errorMessage,
    trailingIcon = {
      if (text.isNotEmpty()) {
        if (visible) PasswordInvisibleIcon(
          onClick = { visible = false }
        ) else PasswordVisibleIcon(
          onClick = { visible = true }
        )
      }
    },
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = keyboardOptions,
    focusManager = focusManager,
    keyboardController = keyboardController,
    isFocused = isFocused
  )
}

@Composable
fun CheckIcon(
  modifier: Modifier = Modifier
) {
  Icon(
    imageVector = Icons.Rounded.Check,
    contentDescription = "search",
    modifier = modifier.size(20.dp),
    tint = MaterialTheme.colorScheme.secondary
  )
}

@Composable
fun ErrorIcon(
  modifier: Modifier = Modifier
) {
  Icon(
    imageVector = Icons.Rounded.Warning,
    contentDescription = "error",
    modifier = modifier.size(20.dp),
    tint = MaterialTheme.colorScheme.error
  )
}

@Composable
fun SearchIcon(
  modifier: Modifier = Modifier
) {
  Icon(
    painter = painterResource(R.drawable.ic_search),
    contentDescription = "search",
    modifier = modifier.size(20.dp)
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
    modifier = modifier
      .size(20.dp)
      .clickableWithoutEffect(onClick)
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
    modifier = modifier
      .size(20.dp)
      .clickableWithoutEffect(onClick)
  )
}

@Preview(widthDp = 1024, showBackground = true)
@Composable
private fun Preview() {
  LunchVoteTheme {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TextField(
          text = "Active",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f)
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f)
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isFocused = true
        )
        TextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isFocused = true
        )
        TextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          enabled = false
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TextField(
          text = "Valid",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = false
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          isError = false
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isError = false,
          isFocused = true
        )
        TextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = false,
          isFocused = true
        )
        TextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = false,
          enabled = false,
          trailingIcon = {
            CheckIcon()
          }
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TextField(
          text = "Error",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = true
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          isError = true
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isError = true,
          isFocused = true
        )
        TextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = true,
          isFocused = true
        )
        TextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = true,
          enabled = false
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TextField(
          text = "Search",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          }
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          }
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          },
          isFocused = true
        )
        TextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          },
          isFocused = true
        )
        TextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          enabled = false,
          leadingIcon = {
            SearchIcon()
          }
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PasswordField(
          text = "Password",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isVisible = true
        )
        PasswordField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f)
        )
        PasswordField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isFocused = true
        )
        PasswordField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isFocused = true
        )
        PasswordField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          enabled = false
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TextField(
          text = "Multiline",
          onTextChange = {},
          hintText = "",
          modifier = Modifier
            .weight(1f)
            .height(300.dp),
          singleLine = false
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier
            .weight(1f)
            .height(300.dp),
          singleLine = false
        )
        TextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier
            .weight(1f)
            .height(300.dp),
          singleLine = false,
          isFocused = true
        )
        TextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier
            .weight(1f)
            .height(300.dp),
          singleLine = false,
          isFocused = true
        )
        TextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier
            .weight(1f)
            .height(300.dp),
          enabled = false,
          singleLine = false
        )
      }
    }
  }
}