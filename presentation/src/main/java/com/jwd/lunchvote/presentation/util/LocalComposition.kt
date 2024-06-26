package com.jwd.lunchvote.presentation.util

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.channels.Channel

internal val LocalSnackbarChannel = staticCompositionLocalOf { Channel<String>() }