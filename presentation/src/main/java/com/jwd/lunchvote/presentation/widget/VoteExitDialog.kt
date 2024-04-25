package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R

@Composable
fun VoteExitDialog(
    onDismiss: () -> Unit,
    onExit: () -> Unit,
    isOwner: Boolean = false
){
    LunchVoteDialog(
        title = "정말 나가시겠습니까?",
        dismissText = "취소",
        onDismissRequest = onDismiss,
        confirmText = "나가기",
        onConfirmation = onExit,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        },
        content = {
            Text(
                if (isOwner) stringResource(id = R.string.lounge_exit_dialog_owner_content)
                    else stringResource(id = R.string.lounge_exit_dialog_member_content),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun VoteExitDialogPreview(){
    LunchVoteTheme {
        VoteExitDialog(onDismiss = {}, onExit = {})
    }
}