package com.jwd.lunchvote.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@Composable
fun VoteExitDialog(
    onDismiss: () -> Unit,
    onExit: () -> Unit
){
    LunchVoteDialog(
        onDismiss = onDismiss,
        content = {
            Image(painter = painterResource(id = R.drawable.ic_warn), contentDescription = "exit_warn")

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                stringResource(R.string.exit_dialog_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.vote_exit_alert),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.weight(1f))

                Button(onDismiss) { Text("취소") }

                Button(
                    onClick = onExit,
                ) { Text("나가기") }
            }
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