package com.jwd.lunchvote.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun<T> Flow<T>.binds(
    viewLifecycleOwner : androidx.lifecycle.LifecycleOwner,
    minActiveState : Lifecycle.State = Lifecycle.State.STARTED,
    block : suspend (T) -> Unit
) = flowWithLifecycle(viewLifecycleOwner.lifecycle, minActiveState).onEach { block(it) }.launchIn(viewLifecycleOwner.lifecycleScope)