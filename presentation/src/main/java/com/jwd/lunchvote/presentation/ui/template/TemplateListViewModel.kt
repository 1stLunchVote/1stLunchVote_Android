package com.jwd.lunchvote.presentation.ui.template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.GetTemplateListUseCase
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListEvent
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListReduce
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.presentation.ui.template.TemplateListContract.TemplateListState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateListViewModel @Inject constructor(
  private val getTemplateListUseCase: GetTemplateListUseCase,
  private val auth: FirebaseAuth,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<TemplateListState, TemplateListEvent, TemplateListReduce, TemplateListSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListState {
    return savedState as? TemplateListState ?: TemplateListState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: TemplateListEvent) {
    when(event) {
      is TemplateListEvent.ScreenInitialize -> launch { initialize() }
      is TemplateListEvent.OnClickBackButton -> sendSideEffect(TemplateListSideEffect.PopBackStack)
      is TemplateListEvent.OnClickTemplate -> sendSideEffect(TemplateListSideEffect.NavigateToEditTemplate(event.templateId))
      is TemplateListEvent.OnClickAddButton -> sendSideEffect(TemplateListSideEffect.OpenAddDialog)

      // DialogEvents
      is TemplateListEvent.OnTemplateNameChange -> setDialogState(event.templateName)
      is TemplateListEvent.OnClickDismissButtonAddDialog -> sendSideEffect(TemplateListSideEffect.CloseDialog)
      is TemplateListEvent.OnClickConfirmButtonAddDialog -> {
        val templateName = currentState.templateName ?: return
        sendSideEffect(TemplateListSideEffect.CloseDialog)
        sendSideEffect(TemplateListSideEffect.NavigateToAddTemplate(templateName))
      }
    }
  }

  override fun reduceState(state: TemplateListState, reduce: TemplateListReduce): TemplateListState {
    return when (reduce) {
      is TemplateListReduce.UpdateTemplateList -> state.copy(templateList = reduce.templateList)
      is TemplateListReduce.UpdateTemplateName -> state.copy(templateName = reduce.templateName)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(TemplateListSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize() {
    val userId = auth.currentUser?.uid ?: throw LoginError.NoUser
    val templateList = getTemplateListUseCase(userId).map { it.asUI() }

    updateState(TemplateListReduce.UpdateTemplateList(templateList))
  }
}