package com.jwd.lunchvote.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.ui.result.ResultContract.*

@Composable
fun ResultRoute(
    viewModel: ResultViewModel = hiltViewModel()
){
    val state: ResultState by viewModel.viewState.collectAsStateWithLifecycle()
}