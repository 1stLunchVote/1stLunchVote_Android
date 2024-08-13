package com.jwd.lunchvote.presentation.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavHost
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.navigation.route
import com.jwd.lunchvote.presentation.util.ConnectionManager
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.SetUserOfflineWorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var dispatcher: CoroutineDispatcher

  @Inject
  lateinit var userStatusRepository: UserStatusRepository

  @Inject
  lateinit var connectionManager: ConnectionManager

  override fun onResume() {
    super.onResume()

    Firebase.auth.currentUser?.uid?.let { userId ->
      lifecycleScope.launch {
        userStatusRepository.setUserOnline(userId)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()
    val setUserOfflineRequest = OneTimeWorkRequestBuilder<SetUserOfflineWorkManager>()
      .setConstraints(constraints)
      .build()

    WorkManager
      .getInstance(this)
      .enqueue(setUserOfflineRequest)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()

    // 뒤로가기로 앱 종료 시 백스택에 들어가지 않고 앱이 종료되도록 설정
    onBackPressedDispatcher.addCallback(
      owner = this,
      onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          finish()
        }
      }
    )

    setContent {
      LunchVoteScreen()
    }
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
        Scaffold(
          snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
          val snackbarChannel: Channel<String> = Channel()
          LaunchedEffect(snackbarChannel) {
            snackbarChannel
              .receiveAsFlow()
              .collectLatest { message ->
                snackbarHostState.showSnackbar(message)
              }
          }
          CompositionLocalProvider(
            value = LocalSnackbarChannel provides snackbarChannel
          ) {
            LunchVoteNavHost(
              startDestination = startDestination,
              connectionManager = connectionManager,
              navController = navController,
              modifier = Modifier.padding(padding)
            )
          }
        }
      }
    }
  }
}