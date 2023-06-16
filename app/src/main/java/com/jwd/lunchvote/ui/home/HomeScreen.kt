package com.jwd.lunchvote.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.home.HomeContract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@Composable
fun HomeRoute(
    navigateToLounge: (String?) -> Unit,
    messageFlow: Flow<String>,
    viewModel: HomeViewModel = hiltViewModel()
){
    val homeState : HomeState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is HomeSideEffect.NavigateToLounge -> {
                    navigateToLounge(it.loungeId)
                }
                is HomeSideEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    LaunchedEffect(Unit){
        messageFlow.filter { it.isNotEmpty() }.collectLatest {
            snackBarHostState.showSnackbar(it)
        }
    }

    HomeScreen(
        homeState = homeState,
        snackBarHostState = snackBarHostState,
        onCreateLounge = { viewModel.sendEvent(HomeEvent.OnCreateLounge) },
        onJoinLounge = { viewModel.sendEvent(HomeEvent.OnJoinLounge) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    homeState: HomeState,
    snackBarHostState: SnackbarHostState,
    onCreateLounge: () -> Unit = {},
    onJoinLounge: () -> Unit = {},
){
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            Button(onClick = onCreateLounge) {
                Text(text = "대기방 생성")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Button(onClick = onJoinLounge) {
                Text(text = "대기방 참여")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview(){
    LunchVoteTheme {
        HomeScreen(
            homeState = HomeState(),
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}