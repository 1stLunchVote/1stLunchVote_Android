package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jwd.lunchvote.presentation.modifier.animatePopUp
import com.jwd.lunchvote.presentation.modifier.outerShadow

@Composable
internal fun Dialog(
  title: String,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  icon: @Composable (() -> Unit)? = null,
  iconColor: Color = MaterialTheme.colorScheme.primary,
  body: String = "",
  closable: Boolean = false,
  canDismiss: Boolean = true,
  content: @Composable (() -> Unit)? = null,
  buttons: @Composable (RowScope.() -> Unit)? = null,
) {
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(
      dismissOnClickOutside = canDismiss,
      dismissOnBackPress = canDismiss,
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = modifier
        .padding(24.dp)
        .fillMaxWidth()
        .outerShadow(
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
          shape = MaterialTheme.shapes.medium,
          offsetY = 4.dp,
          blur = 16.dp,
          spread = (-4).dp
        )
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (closable) {
        IconButton(
          onClick = onDismissRequest,
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopEnd)
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.outline
          )
        }
      }
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          DialogIcon(
            icon = icon,
            iconColor = iconColor
          )
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
          )
          Text(
            text = body,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
            style = MaterialTheme.typography.bodyMedium
          )
        }
        content?.invoke()
        buttons?.let {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            buttons()
          }
        }
      }
    }
  }
}

@Composable
private fun DialogIcon(
  icon: @Composable (() -> Unit)?,
  iconColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.size(48.dp),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .background(iconColor.copy(alpha = 0.16f), CircleShape)
        .animatePopUp(48.dp)
    )
    Box(
      modifier = Modifier
        .background(iconColor.copy(alpha = 0.16f), CircleShape)
        .animatePopUp(32.dp)
    )
    Box(
      modifier = Modifier.animatePopUp(24.dp),
      contentAlignment = Alignment.Center
    ) {
      CompositionLocalProvider(LocalContentColor provides iconColor) {
        icon?.invoke() ?: Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = "Check"
        )
      }
    }
  }
}

@Composable
internal fun RowScope.DialogButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary,
  enabled: Boolean = true,
  isDismiss: Boolean = false
) {
  if (isDismiss.not()) {
    Button(
      onClick = onClick,
      enabled = enabled,
      modifier = modifier.weight(1f),
      shape = MaterialTheme.shapes.small,
      colors = ButtonDefaults.buttonColors(
        containerColor = color,
        contentColor = MaterialTheme.colorScheme.onPrimary
      )
    ) {
      Text(text)
    }
  } else {
    OutlinedButton(
      onClick = onClick,
      modifier = modifier
        .weight(1f)
        .alpha(0.64f),
      enabled = enabled,
      shape = MaterialTheme.shapes.small,
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = color,
      )
    ) {
      Text(text)
    }
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    Dialog(
      title = "게시물 삭제",
      onDismissRequest = {},
      body = "정말 삭제하시겠습니까?\n삭제할 경우 되돌릴 수 없습니다.",
      icon = {
        Icon(
          imageVector = Icons.Rounded.Delete,
          contentDescription = "Delete"
        )
      },
      iconColor = MaterialTheme.colorScheme.error
    ) {
      DialogButton(
        text = "취소",
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error,
        isDismiss = true
      )
      DialogButton(
        text = "확인",
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error
      )
    }
    Screen {}
  }
}