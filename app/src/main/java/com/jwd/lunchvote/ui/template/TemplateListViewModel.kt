package com.jwd.lunchvote.ui.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.template.GetTemplatesUseCase
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListDialogState
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListDialogState.AddTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickAddButton
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickBackButton
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickDismissButton
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.OnClickTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListEvent.StartInitialize
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListReduce
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListReduce.Initialize
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListReduce.UpdateLoading
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.NavigateToAddTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.NavigateToEditTemplate
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect.PopBackStack
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateListViewModel @Inject constructor(
  private val getTemplatesUseCase: GetTemplatesUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<TemplateListState, TemplateListEvent, TemplateListReduce, TemplateListSideEffect, TemplateListDialogState>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListState {
    return savedState as? TemplateListState ?: TemplateListState()
  }

  override fun handleEvents(event: TemplateListEvent) {
    when(event) {
      is StartInitialize -> viewModelScope.launch { initialize() }
      is OnClickBackButton -> sendSideEffect(PopBackStack)
      is OnClickTemplate -> sendSideEffect(NavigateToEditTemplate(event.templateId))
      is OnClickAddButton -> toggleDialog(
        AddTemplate(
          onClickConfirm = { templateName -> sendSideEffect(NavigateToAddTemplate(templateName)) }
        )
      )
      is OnClickDismissButton -> toggleDialog(null)
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is UpdateLoading -> state.copy(loading = reduce.loading)
      is Initialize -> reduce.state
    }
  }

  private suspend fun initialize() {
    updateState(UpdateLoading(true))

    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    val templateList = getTemplatesUseCase.invoke(userId)
    updateState(
      Initialize(
        TemplateListState(
          loading = false,
          templateList = templateList.map { TemplateUIModel(it) }
        )
      )
    )
  }
}