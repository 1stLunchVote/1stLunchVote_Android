package com.jwd.lunchvote.presentation.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavHost
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.navigation.route
import com.jwd.lunchvote.presentation.util.Connection
import com.jwd.lunchvote.presentation.util.Connection.LOST
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.NetworkLostDialog
import com.jwd.lunchvote.presentation.work.UserStatusWorker
import com.jwd.lunchvote.presentation.work.UserStatusWorker.Companion.IS_ONLINE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    setBackPressToTerminateApp()
    setContent { LunchVoteScreen() }
  }

  @Composable
  private fun LunchVoteScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    val startDestination = when {
      Firebase.auth.currentUser != null -> LunchVoteNavRoute.Home.route
      Firebase.auth.isSignInWithEmailLink(intent.data.toString()) -> LunchVoteNavRoute.Password.route
      else -> LunchVoteNavRoute.Login.route
    }

    LunchVoteTheme {
      Surface(
        modifier = Modifier.fillMaxSize()
      ) {
        val connectionState by Connection.connectionState.collectAsStateWithLifecycle()
        if (connectionState == LOST) NetworkLostDialog { finish() }

        Scaffold(
          snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
          val snackbarChannel: Channel<String> = Channel()
          LaunchedEffect(snackbarChannel) {
            snackbarChannel.receiveAsFlow()
              .collectLatest { message ->
                snackbarHostState.showSnackbar(message)
              }
          }

          CompositionLocalProvider(
            value = LocalSnackbarChannel provides snackbarChannel
          ) {
            LunchVoteNavHost(
              navController = navController,
              startDestination = startDestination,
              modifier = Modifier.padding(padding)
            )
          }
        }
      }
    }
  }

  private fun MainActivity.setBackPressToTerminateApp() {
    onBackPressedDispatcher.addCallback(
      owner = this,
      onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() { finish() }
      }
    )
  }

  override fun onResume() {
    super.onResume()
    setUserStatus(isOnline = true)
  }

  override fun onDestroy() {
    super.onDestroy()
    setUserStatus(isOnline = false)
  }

  private fun setUserStatus(isOnline: Boolean) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()
    val data = Data.Builder()
      .putBoolean(IS_ONLINE, isOnline)
      .build()

    val request = OneTimeWorkRequestBuilder<UserStatusWorker>()
      .setConstraints(constraints)
      .setInputData(data)
      .build()

    WorkManager
      .getInstance(this)
      .enqueue(request)
  }
}