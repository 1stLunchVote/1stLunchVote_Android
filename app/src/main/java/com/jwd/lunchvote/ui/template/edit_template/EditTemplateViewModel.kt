package com.jwd.lunchvote.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.domain.usecase.template.GetTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.GetTemplatesUseCase
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.home.HomeContract
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val getTemplateUseCase: GetTemplateUseCase,
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): BaseStateViewModel<EditTemplateState, EditTemplateEvent, EditTemplateReduce, EditTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): EditTemplateState {
    return savedState as? EditTemplateState ?: EditTemplateState()
  }

  init {
    val templateId = checkNotNull(savedStateHandle.get<String>("templateId"))
    sendEvent(EditTemplateEvent.StartInitialize(templateId))
  }

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is EditTemplateEvent.StartInitialize -> {
        updateState(EditTemplateReduce.UpdateLoading(true))
        CoroutineScope(ioDispatcher).launch {
          initialize(event.templateId)
        }
      }
      is EditTemplateEvent.OnClickBackButton -> sendSideEffect(EditTemplateSideEffect.PopBackStack())
    }
  }

  override fun reduceState(state: EditTemplateState, reduce: EditTemplateReduce): EditTemplateState {
    return when (reduce) {
      is EditTemplateReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is EditTemplateReduce.Initialize -> reduce.state
    }
  }

  private suspend fun initialize(templateId: String) {
    val template = getTemplateUseCase.invoke(templateId)

    updateState(EditTemplateReduce.Initialize(
      EditTemplateState(
        template = TemplateUIModel(template)
      )
    ))
  }
}