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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LunchVoteTextField(
    modifier: Modifier = Modifier,
    text: String,
    hintText: String,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextChanged: (String) -> Unit,
    keyboardEnterNext: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusManager : FocusManager = LocalFocusManager.current,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
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
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
        ),
        visualTransformation = visualTransformation
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun LunchVoteTextFieldPreview() {
    LunchVoteTheme {
        LunchVoteTextField(text = "", hintText = "hint", onTextChanged = {})
    }
}