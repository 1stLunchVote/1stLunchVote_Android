package com.jwd.lunchvote.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.home.HomeContract.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
    navigateToLounge: (String?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
){
    val homeState : HomeState by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is HomeSideEffect.NavigateToLounge -> {
                    navigateToLounge(it.loungeId)
                }
            }
        }
    }

    HomeScreen(
        homeState = homeState,
        onCreateLounge = { viewModel.sendEvent(HomeEvent.OnCreateLounge) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    homeState: HomeState,
    onCreateLounge: () -> Unit = {},
){
    Scaffold(
        floatingActionButton = {
            Button(onClick = onCreateLounge) {
                Text(text = "대기방으로 가기")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview(){
    LunchVoteTheme {
        HomeScreen(
            homeState = HomeState()
        )
    }
}