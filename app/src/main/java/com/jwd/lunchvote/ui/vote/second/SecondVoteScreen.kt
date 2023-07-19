package com.jwd.lunchvote.ui.vote.second

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jwd.lunchvote.widget.ProgressTopBar

@Composable
fun SecondVoteRoute(

){
    
    SecondVoteScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecondVoteScreen(

){
    Scaffold(
        topBar = {
            ProgressTopBar(title = "2차 투표")
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {

        }
    }
}