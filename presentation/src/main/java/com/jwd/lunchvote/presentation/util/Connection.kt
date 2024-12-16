package com.jwd.lunchvote.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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

object Connection {
  private var connectivityManager: ConnectivityManager? = null
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

  const val CONNECTED = 1
  const val LOST = -1

  fun initialize(context: Context) {
    connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
  }

  private val _connectionFlow: Flow<Int> = callbackFlow {
    val networkCallback = object : ConnectivityManager.NetworkCallback() {

      override fun onAvailable(network: Network) {
        super.onAvailable(network)
        scope.launch {
          channel.trySend(CONNECTED)
        }
      }

      override fun onLost(network: Network) {
        super.onLost(network)
        scope.launch {
          channel.trySend(LOST)
        }
      }
    }

    connectivityManager?.registerDefaultNetworkCallback(networkCallback)

    awaitClose {
      connectivityManager?.unregisterNetworkCallback(networkCallback)
    }
  }.conflate()

  val connectionState: StateFlow<Int>
    get() = _connectionFlow.stateIn(
      scope = scope,
      started = SharingStarted.Lazily,
      initialValue = connectivityManager?.getNetworkCapabilities(connectivityManager?.activeNetwork)?.let {
        when {
          it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> CONNECTED
          it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> CONNECTED
          it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> CONNECTED
          else -> LOST
        }
      } ?: LOST
    )

  val currentState: Int = connectionState.value
}