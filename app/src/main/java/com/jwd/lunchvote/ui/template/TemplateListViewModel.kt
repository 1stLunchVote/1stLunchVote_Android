package com.jwd.lunchvote.ui.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.domain.usecase.template.GetTemplatesUseCase
import com.jwd.lunchvote.model.TemplateUIModel
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
  private val getTemplatesUseCase: GetTemplatesUseCase,
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
): BaseStateViewModel<TemplateListState, TemplateListEvent, TemplateListReduce, TemplateListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListState {
    return savedState as? TemplateListState ?: TemplateListState()
  }

  override fun handleEvents(event: TemplateListEvent) {
    when(event) {
      is TemplateListEvent.StartInitialize -> {
        updateState(TemplateListReduce.UpdateLoading(true))
        CoroutineScope(dispatcher).launch {
          initialize()
        }
      }
      is TemplateListEvent.OnClickBackButton -> sendSideEffect(TemplateListSideEffect.PopBackStack)
      is TemplateListEvent.OnClickTemplate -> sendSideEffect(TemplateListSideEffect.NavigateToEditTemplate(event.templateId))
      is TemplateListEvent.OnClickAddButton -> updateState(TemplateListReduce.UpdateDialogState(true))
      is TemplateListEvent.SetTemplateName -> updateState(TemplateListReduce.UpdateTemplateName(event.templateName))
      is TemplateListEvent.OnClickDismiss -> updateState(TemplateListReduce.UpdateDialogState(false))
      is TemplateListEvent.OnClickConfirm -> {
        updateState(TemplateListReduce.UpdateDialogState(false))
        sendSideEffect(TemplateListSideEffect.NavigateToAddTemplate(currentState.templateName.trim()))
      }
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is TemplateListReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is TemplateListReduce.Initialize -> reduce.state
      is TemplateListReduce.UpdateTemplateName -> state.copy(templateName = reduce.templateName)
      is TemplateListReduce.UpdateDialogState -> state.copy(dialogState = reduce.dialogState)
    }
  }

  private suspend fun initialize() {
    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    val templateList = getTemplatesUseCase.invoke(userId)

    updateState(TemplateListReduce.Initialize(
      TemplateListState(
        loading = false,
        templateList = templateList.map { TemplateUIModel(it) }
      )
    ))
  }
}