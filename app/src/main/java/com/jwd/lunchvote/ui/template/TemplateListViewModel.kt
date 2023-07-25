package com.jwd.lunchvote.ui.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListReduce
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateListViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): BaseStateViewModel<TemplateListState, TemplateListEvent, TemplateListReduce, TemplateListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListState {
    return savedState as? TemplateListState ?: TemplateListState()
  }

  init {
    sendEvent(TemplateListEvent.StartInitialize)
  }

  override fun handleEvents(event: TemplateListEvent) {
    when(event) {
      is TemplateListEvent.StartInitialize -> {
        updateState(TemplateListReduce.UpdateLoading(true))
        CoroutineScope(ioDispatcher).launch {
          initialize()
        }
      }
      is TemplateListEvent.OnClickBackButton -> sendSideEffect(TemplateListSideEffect.PopBackStack)
      is TemplateListEvent.OnClickTemplate -> sendSideEffect(TemplateListSideEffect.NavigateToEditTemplate(event.templateId))
      is TemplateListEvent.OnClickAddButton -> sendSideEffect(TemplateListSideEffect.NavigateToAddTemplate)
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is TemplateListReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is TemplateListReduce.Initialize -> reduce.state
    }
  }

  private suspend fun initialize() {
    updateState(TemplateListReduce.Initialize(
      TemplateListState(

      )
    ))
  }
}