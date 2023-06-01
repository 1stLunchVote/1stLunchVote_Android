package com.jwd.lunchvote.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.ui.home.HomeContract.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
){
    val homeState : HomeState by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {

        }
    }

    HomeScreen(homeState = homeState)
}

@Composable
private fun HomeScreen(
    homeState: HomeState
){

}