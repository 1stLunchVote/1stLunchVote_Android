package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun LunchVoteTextField(
  text: String,
  onTextChange: (String) -> Unit,
  hintText: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isError: Boolean? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
  maxLines: Int = 1,
  focusManager: FocusManager = LocalFocusManager.current,
  keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
  isFocused: Boolean = false
) {
  var focused by remember { mutableStateOf(isFocused) }
  LaunchedEffect(isFocused) {
    focused = enabled && isFocused
  }

  val color = when (isError) {
    null -> MaterialTheme.colorScheme.primary
    false -> MaterialTheme.colorScheme.tertiary
    true -> MaterialTheme.colorScheme.error
  }

  BasicTextField(
    value = text,
    onValueChange = onTextChange,
    modifier = modifier.onFocusChanged { focused = it.isFocused },
    enabled = enabled,
    textStyle = MaterialTheme.typography.bodyMedium,
    visualTransformation = visualTransformation,
    singleLine = maxLines == 1,
    maxLines = maxLines,
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
    )
  ) { innerTextField ->
    Row(
      modifier = modifier
        .conditional(condition = focused, modifierIf = {
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
        .conditional(condition = text.isNotEmpty() || focused, modifierIf = {
          border(1.dp, color, MaterialTheme.shapes.small)
        }, modifierElse = {
          background(MaterialTheme.colorScheme.onBackground.copy(0.1f), MaterialTheme.shapes.small)
        })
        .conditional(enabled.not()) {
          alpha(0.32f)
        }
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      leadingIcon?.invoke()
      Box(
        modifier = Modifier
          .weight(1f)
          .height(20.dp),
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
      trailingIcon?.invoke()
    }
  }
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
        LunchVoteTextField(
          text = "Active",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f)
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f)
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isFocused = true
        )
        LunchVoteTextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isFocused = true
        )
        LunchVoteTextField(
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
        LunchVoteTextField(
          text = "Valid",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = false
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          isError = false
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isError = false,
          isFocused = true
        )
        LunchVoteTextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = false,
          isFocused = true
        )
        LunchVoteTextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = false,
          enabled = false
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        LunchVoteTextField(
          text = "Error",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = true
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          isError = true
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          isError = true,
          isFocused = true
        )
        LunchVoteTextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          isError = true,
          isFocused = true
        )
        LunchVoteTextField(
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
        LunchVoteTextField(
          text = "Search",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          }
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          }
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          },
          isFocused = true
        )
        LunchVoteTextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          leadingIcon = {
            SearchIcon()
          },
          isFocused = true
        )
        LunchVoteTextField(
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
        LunchVoteTextField(
          text = "Password",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          trailingIcon = {
            PasswordVisibleIcon({})
          }
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Blank",
          modifier = Modifier.weight(1f),
          trailingIcon = {
            PasswordVisibleIcon({})
          },
          visualTransformation = PasswordVisualTransformation()
        )
        LunchVoteTextField(
          text = "",
          onTextChange = {},
          hintText = "Focused",
          modifier = Modifier.weight(1f),
          trailingIcon = {
            PasswordVisibleIcon({})
          },
          visualTransformation = PasswordVisualTransformation(),
          isFocused = true
        )
        LunchVoteTextField(
          text = "Typed",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          trailingIcon = {
            PasswordVisibleIcon({})
          },
          visualTransformation = PasswordVisualTransformation(),
          isFocused = true
        )
        LunchVoteTextField(
          text = "Disabled",
          onTextChange = {},
          hintText = "",
          modifier = Modifier.weight(1f),
          enabled = false,
          trailingIcon = {
            PasswordVisibleIcon({})
          },
          visualTransformation = PasswordVisualTransformation()
        )
      }
    }
  }
}