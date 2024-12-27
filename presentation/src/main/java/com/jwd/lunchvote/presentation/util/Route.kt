package com.jwd.lunchvote.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.jwd.lunchvote.presentation.base.ViewModelContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <SE: ViewModelContract.SideEffect> SideEffectHandler(
  sideEffect: Flow<SE>,
  handler: suspend (sideEffect: SE) -> Unit
) {
  LaunchedEffect(sideEffect) {
    sideEffect.collectLatest { handler(it) }
  }
}

@Composable
fun <S: ViewModelContract.State> Dialogs(
  state: S,
  dialogs: @Composable S.() -> Unit
) {
  state.dialogs()
}