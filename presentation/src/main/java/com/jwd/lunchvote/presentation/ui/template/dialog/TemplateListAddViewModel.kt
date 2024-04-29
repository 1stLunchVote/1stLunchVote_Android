package com.jwd.lunchvote.presentation.ui.template.dialog

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddContract.TemplateListAddEvent
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddContract.TemplateListAddReduce
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddContract.TemplateListAddSideEffect
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddContract.TemplateListAddState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TemplateListAddViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<TemplateListAddState, TemplateListAddEvent, TemplateListAddReduce, TemplateListAddSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): TemplateListAddState {
    return savedState as? TemplateListAddState ?: TemplateListAddState()
  }

  override fun handleEvents(event: TemplateListAddEvent) {
    when(event) {
      is TemplateListAddEvent.OnTemplateNameChange -> updateState(TemplateListAddReduce.UpdateTemplateName(event.templateName))
      is TemplateListAddEvent.OnClickDismissButton -> sendSideEffect(TemplateListAddSideEffect.PopBackStack)
      is TemplateListAddEvent.OnClickConfirmButton -> sendSideEffect(TemplateListAddSideEffect.NavigateToAddTemplate(currentState.templateName.trim()))
    }
  }

  override fun reduceState(state: TemplateListAddState, reduce: TemplateListAddReduce): TemplateListAddState {
    return when(reduce) {
      is TemplateListAddReduce.UpdateTemplateName -> state.copy(templateName = reduce.templateName)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(TemplateListAddSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }
}