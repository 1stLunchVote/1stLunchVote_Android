package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>DropDownMenu(
  list: List<T>,
  selected: T?,
  onItemSelected: (T) -> Unit,
  getItemName: (T) -> String,
  hintText: String,
  modifier: Modifier = Modifier,
  placeholder: String = ""
) {
  var expended by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expended,
    onExpandedChange = { expended = it },
    modifier = modifier
  ) {
    if (list.isEmpty()) {
      TextField(
        text = "",
        onTextChange = {},
        hintText = placeholder,
        enabled = false,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) }
      )
    } else {
      TextField(
        text = if (selected != null) getItemName(selected) else "",
        onTextChange = {},
        hintText = hintText,
        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
        readOnly = true,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) }
      )
      ExposedDropdownMenu(
        expanded = expended,
        onDismissRequest = { expended = false },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
      ) {
        list.forEach { item ->
          DropdownMenuItem(
            text = { Text(text = getItemName(item)) },
            onClick = {
              onItemSelected(item)
              expended = false
            },
            enabled = item != selected
          )
        }
      }
    }
  }
}

@Preview(widthDp = 768, showBackground = true)
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
        DropDownMenu(
          list = emptyList(),
          selected = null,
          onItemSelected = {},
          getItemName = { it },
          hintText = "아이템을 선택해주세요.",
          modifier = Modifier.weight(1f),
          placeholder = "아이템이 없습니다."
        )
        DropDownMenu(
          list = listOf("Item 1", "Item 2", "Item 3"),
          selected = null,
          onItemSelected = {},
          getItemName = { it },
          hintText = "아이템을 선택해주세요.",
          modifier = Modifier.weight(1f)
        )
        DropDownMenu(
          list = listOf("Item 1", "Item 2", "Item 3"),
          selected = "Item 1",
          onItemSelected = {},
          getItemName = { it },
          hintText = "아이템을 선택해주세요.",
          modifier = Modifier.weight(1f)
        )
      }

    }
  }
}