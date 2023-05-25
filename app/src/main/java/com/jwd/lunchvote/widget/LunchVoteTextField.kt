package com.jwd.lunchvote.widget

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchVoteTextField(
    modifier: Modifier = Modifier,
    text: String,
    hintText: String,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextChanged: (String) -> Unit,
    keyboardEnterNext: Boolean = false,
){
    OutlinedTextField(
        modifier = modifier,
        label = { Text(text = hintText) },
        value = text,
        maxLines = maxLines,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedLabelColor = MaterialTheme.colorScheme.outline
        ),
        onValueChange = onTextChanged,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                if(keyboardEnterNext){
                    KeyboardActions.Default.onNext
                } else {
                    KeyboardActions.Default.onDone
                }
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun LunchVoteTextFieldPreview() {
    LunchVoteTheme {
        LunchVoteTextField(text = "", hintText = "hint", onTextChanged = {})
    }
}