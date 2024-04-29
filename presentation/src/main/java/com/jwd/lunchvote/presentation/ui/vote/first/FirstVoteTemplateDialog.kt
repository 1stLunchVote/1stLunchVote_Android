package com.jwd.lunchvote.presentation.ui.vote.first

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstVoteTemplateDialog(
  modifier: Modifier = Modifier,
  popBackStack: () -> Unit = {},
) {
  var expended by remember { mutableStateOf(false) }
  var chosenTemplate by remember { mutableStateOf<TemplateUIModel?>(null) }

  LunchVoteDialog(
    title = "템플릿을 선택해주세요.",
    dismissText = "건너뛰기",
    onDismissRequest = popBackStack,
    confirmText = "선택",
    onConfirmation = {
      // TODO: 적용 필요
      /*firstVoteDialogState.selectTemplate(chosenTemplate)
      onClickDismissButton()*/
    },
    confirmEnabled = chosenTemplate != null,
    content = {
      ExposedDropdownMenuBox(
        expanded = expended,
        onExpandedChange = { expended = it }
      ) {
        OutlinedTextField(
          value = chosenTemplate?.name ?: "",
          onValueChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(),
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expended) },
          placeholder = { Text("템플릿을 선택해주세요.") }
        )
        ExposedDropdownMenu(
          expanded = expended,
          onDismissRequest = { expended = false }
        ) {
//          firstVoteDialogState.templateList.forEach { template ->
//            DropdownMenuItem(
//              text = { Text(template.name) },
//              onClick = {
//                chosenTemplate = template
//                expended = false
//              }
//            )
//          }
          // TODO: 템플릿 리스트 받아오기 구현
        }
      }
    }
  )
}