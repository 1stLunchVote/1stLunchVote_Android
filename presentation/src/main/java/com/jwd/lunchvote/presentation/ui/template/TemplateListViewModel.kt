package com.jwd.lunchvote.presentation.ui.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.template.GetTemplatesUseCase
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListDialogState
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListReduce
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListState
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
      is TemplateListEvent.StartInitialize -> viewModelScope.launch { initialize() }
      is TemplateListEvent.OnClickBackButton -> sendSideEffect(TemplateListSideEffect.PopBackStack)
      is TemplateListEvent.OnClickTemplate -> sendSideEffect(
        TemplateListSideEffect.NavigateToEditTemplate(
          event.templateId
        )
      )
      is TemplateListEvent.OnClickAddButton -> toggleDialog(
        TemplateListDialogState.AddTemplate(
          onClickConfirm = { templateName -> sendSideEffect(
            TemplateListSideEffect.NavigateToAddTemplate(
              templateName
            )
          ) }
        )
      )
      is TemplateListEvent.OnClickDismissButton -> toggleDialog(null)
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is TemplateListReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is TemplateListReduce.Initialize -> reduce.state
    }
  }

  private suspend fun initialize() {
    updateState(TemplateListReduce.UpdateLoading(true))

    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    val templateList = getTemplatesUseCase.invoke(userId)
    updateState(
      TemplateListReduce.Initialize(
        TemplateListState(
          loading = false,
          templateList = templateList.map { TemplateUIModel(it) }
        )
      )
    )
  }
}