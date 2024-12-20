package com.jwd.lunchvote.presentation.screen.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.AddDialogEvent
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.AddDialogReduce
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.AddDialogState
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListReduce
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.screen.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class TemplateListViewModel @Inject constructor(
  private val templateRepository: TemplateRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<TemplateListState, TemplateListEvent, TemplateListReduce, TemplateListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListState {
    return savedState as? TemplateListState ?: TemplateListState()
  }

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: TemplateListEvent) {
    when(event) {
      is TemplateListEvent.ScreenInitialize -> launch { initialize() }

      is TemplateListEvent.OnClickBackButton -> sendSideEffect(TemplateListSideEffect.PopBackStack)
      is TemplateListEvent.OnClickTemplate -> sendSideEffect(TemplateListSideEffect.NavigateToEditTemplate(event.templateId))
      is TemplateListEvent.OnClickAddButton -> updateState(TemplateListReduce.UpdateAddDialogState(AddDialogState()))
      is AddDialogEvent -> handleAddDialogEvents(event)
    }
  }

  private fun handleAddDialogEvents(event: AddDialogEvent) {
    when(event) {
      is AddDialogEvent.OnTemplateNameChange -> updateState(AddDialogReduce.UpdateTemplateName(event.templateName))
      is AddDialogEvent.OnClickCancelButton -> updateState(TemplateListReduce.UpdateAddDialogState(null))
      is AddDialogEvent.OnClickAddButton -> launch { addTemplate() }
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is TemplateListReduce.UpdateTemplateList -> state.copy(templateList = reduce.templateList)
      is TemplateListReduce.UpdateAddDialogState -> state.copy(addDialogState = reduce.addDialogState)
      is AddDialogReduce -> state.copy(addDialogState = reduceAddDialogState(state.addDialogState, reduce))
    }
  }

  private fun reduceAddDialogState(state: AddDialogState?, reduce: AddDialogReduce): AddDialogState? {
    return when(reduce) {
      is AddDialogReduce.UpdateTemplateName -> state?.copy(templateName = reduce.templateName)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(TemplateListSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val templateList = templateRepository.getTemplateList(userId).map { it.asUI() }

    updateState(TemplateListReduce.UpdateTemplateList(templateList))
  }

  private fun addTemplate() {
    val dialogState = currentState.addDialogState ?: return
    updateState(TemplateListReduce.UpdateAddDialogState(null))

    sendSideEffect(TemplateListSideEffect.NavigateToAddTemplate(dialogState.templateName))
  }
}