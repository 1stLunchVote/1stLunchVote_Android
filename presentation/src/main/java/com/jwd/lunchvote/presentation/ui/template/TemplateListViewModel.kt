package com.jwd.lunchvote.presentation.ui.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.GetTemplateListUseCase
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListReduce
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TemplateListViewModel @Inject constructor(
  private val getTemplateListUseCase: GetTemplateListUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<TemplateListState, TemplateListEvent, TemplateListReduce, TemplateListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListState {
    return savedState as? TemplateListState ?: TemplateListState()
  }

  init {
    launch {
      initialize()
    }
  }

  override fun handleEvents(event: TemplateListEvent) {
    when(event) {
      is TemplateListEvent.OnClickBackButton -> sendSideEffect(TemplateListSideEffect.PopBackStack)
      is TemplateListEvent.OnClickTemplate -> sendSideEffect(TemplateListSideEffect.NavigateToEditTemplate(event.templateId))
      is TemplateListEvent.OnClickAddButton -> sendSideEffect(TemplateListSideEffect.OpenAddDialog)
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is TemplateListReduce.Initialize -> reduce.state
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(TemplateListSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize() {
    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    val templateList = getTemplateListUseCase.invoke(userId)
    updateState(
      TemplateListReduce.Initialize(
        TemplateListState(
          templateList = templateList.map { TemplateUIModel(it) }
        )
      )
    )
  }
}