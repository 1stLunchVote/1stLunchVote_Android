package com.jwd.lunchvote.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import kotlinx.coroutines.delay


@Composable
fun ProgressTopBar(
    title: String,
    animationDuration : Int = 60000,
    onProgressComplete : () -> Unit = {}
){
    var progress by remember { mutableStateOf(0f) }

    val seconds = animationDuration / 1000f
    LaunchedEffect(Unit){
        while (progress < 1f){
            progress += 1f / seconds
            delay(1000)

            if (progress >= 1f) onProgressComplete()
        }
    }

    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    Column {
        LunchVoteTopBar(
            title = title,
            popBackStack = {},
            navIconVisible = false
        )

        LinearProgressIndicator(
            progress = progressAnimation,
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressTopBarPreview(){
    LunchVoteTheme {
        ProgressTopBar(title = "2차 투표")
    }
}