package com.jwd.lunchvote.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectionManager @Inject constructor(
  @ApplicationContext context: Context,
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
  private val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
) {
  
  companion object {
    const val AVAILABLE = 1
    const val LOST = -1
  }

  private val _connectionFlow: Flow<Int> = callbackFlow {

    val networkCallback = object : ConnectivityManager.NetworkCallback() {

      override fun onAvailable(network: Network) {
        super.onAvailable(network)
        scope.launch {
          channel.trySend(AVAILABLE)
        }
      }

      override fun onLost(network: Network) {
        super.onLost(network)
        scope.launch {
          channel.trySend(LOST)
        }
      }
    }

    connectivityManager.registerDefaultNetworkCallback(networkCallback)

    awaitClose {
      connectivityManager.unregisterNetworkCallback(networkCallback)
    }
  }.conflate()

  val connectionState: StateFlow<Int> = _connectionFlow.stateIn(
    scope = scope,
    started = SharingStarted.Lazily,
    initialValue = getConnectionState()
  )
  val currentState: Int get() = connectionState.value

  private fun getConnectionState(): Int {
    val activeNetwork = connectivityManager.activeNetwork
    connectivityManager.getNetworkCapabilities(activeNetwork)?.let {
      return when {
        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> AVAILABLE
        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> AVAILABLE
        it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> AVAILABLE
        else -> LOST
      }
    } ?: return LOST
  }
}