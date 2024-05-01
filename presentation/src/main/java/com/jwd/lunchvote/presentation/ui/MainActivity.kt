package com.jwd.lunchvote.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavHost
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.navigation.route
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var firebaseAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()

    setContent {
      val snackbarHostState = remember { SnackbarHostState() }
      val navController = rememberNavController()

      val startDestination =
        if (firebaseAuth.currentUser != null) LunchVoteNavRoute.Home.route
        else if (Firebase.auth.isSignInWithEmailLink(intent.data.toString())) LunchVoteNavRoute.Password.route
        else LunchVoteNavRoute.Login.route

      LunchVoteTheme {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
          ) { padding ->
            val snackbarChannel: Channel<String> = Channel()
            LaunchedEffect(snackbarChannel) {
              snackbarChannel.receiveAsFlow().collectLatest { message ->
                snackbarHostState.showSnackbar(message)
              }
            }
            LunchVoteNavHost(
              startDestination = startDestination,
              showSnackBar = { snackbarChannel.send(it) },
              navController = navController,
              modifier = Modifier.padding(padding)
            )
          }
        }
      }
    }
  }
}