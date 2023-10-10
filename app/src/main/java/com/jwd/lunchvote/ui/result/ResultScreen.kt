package com.jwd.lunchvote.ui.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.result.ResultContract.*
import com.jwd.lunchvote.widget.LunchVoteTopBar

@Composable
fun ResultRoute(
    viewModel: ResultViewModel = hiltViewModel()
){
    val state: ResultState by viewModel.viewState.collectAsStateWithLifecycle()

    ResultScreen(
        state = state,
        onEventAction = viewModel::sendEvent
    )
}

@Composable
private fun ResultScreen(
    state: ResultState,
    onEventAction: (ResultEvent) -> Unit = {}
){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LunchVoteTopBar(
                title = stringResource(id = R.string.result_toolbar_title),
                navIconVisible = false
            )
        },
    ) {padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(padding)
                    .align(Alignment.Center)
            ){
                val (text, image, ratio, name, button) = createRefs()

                Text(text = stringResource(id = R.string.result_title),
                    modifier = Modifier.constrainAs(text) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                AsyncImage(
                    model = state.image,
                    contentDescription = "menu_image",
                    modifier = Modifier
                        .size(156.dp)
                        .constrainAs(image) {
                            top.linkTo(text.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                Text(
                    text = state.name,
                    modifier = Modifier.constrainAs(name) {
                        top.linkTo(image.bottom, margin = 48.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

    }
}

@Preview(showSystemUi = true)
@Composable
private fun ResultScreenPreview(){
    LunchVoteTheme {
        ResultScreen(
            ResultState(
                image = "",
                name = "치킨"
            )
        )
    }
}
