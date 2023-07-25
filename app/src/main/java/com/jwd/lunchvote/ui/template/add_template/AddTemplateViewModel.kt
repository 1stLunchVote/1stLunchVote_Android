package com.jwd.lunchvote.ui.template.add_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): BaseStateViewModel<AddTemplateState, AddTemplateEvent, AddTemplateReduce, AddTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): AddTemplateState {
    return savedState as? AddTemplateState ?: AddTemplateState()
  }

  init {
    sendEvent(AddTemplateEvent.StartInitialize)
  }

  override fun handleEvents(event: AddTemplateEvent) {
    when(event) {
      is AddTemplateEvent.StartInitialize -> {
        updateState(AddTemplateReduce.UpdateLoading(true))
        CoroutineScope(ioDispatcher).launch {
          initialize()
        }
      }
      is AddTemplateEvent.OnClickBackButton -> sendSideEffect(AddTemplateSideEffect.PopBackStack())
    }
  }

  override fun reduceState(state: AddTemplateState, reduce: AddTemplateReduce): AddTemplateState {
    return when (reduce) {
      is AddTemplateReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is AddTemplateReduce.Initialize -> reduce.state
    }
  }

  private suspend fun initialize() {
    updateState(AddTemplateReduce.Initialize(
      AddTemplateState(

      )
    ))
  }
}