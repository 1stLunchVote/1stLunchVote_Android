package com.jwd.lunchvote.presentation.ui.template.dialog

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class TemplateListAddContract {
  @Parcelize
  data class TemplateListAddState(
    val templateName: String = ""
  ) : ViewModelContract.State, Parcelable

  sealed interface TemplateListAddEvent : ViewModelContract.Event {
    data class OnTemplateNameChange(val templateName: String) : TemplateListAddEvent
    data object OnClickDismissButton : TemplateListAddEvent
    data object OnClickConfirmButton : TemplateListAddEvent
  }

  sealed interface TemplateListAddReduce : ViewModelContract.Reduce {
    data class UpdateTemplateName(val templateName: String) : TemplateListAddReduce
  }

  sealed interface TemplateListAddSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : TemplateListAddSideEffect
    data class NavigateToAddTemplate(val templateName: String) : TemplateListAddSideEffect
    data class ShowSnackBar(val message: UiText) : TemplateListAddSideEffect
  }
}