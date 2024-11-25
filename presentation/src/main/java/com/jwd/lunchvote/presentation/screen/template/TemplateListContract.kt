package com.jwd.lunchvote.presentation.screen.template

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class TemplateListContract {
  @Parcelize
  data class TemplateListState(
    val templateList: List<TemplateUIModel> = emptyList(),

    val addDialogState: AddDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TemplateListEvent : ViewModelContract.Event {
    data object ScreenInitialize : TemplateListEvent

    data object OnClickBackButton : TemplateListEvent
    data class OnClickTemplate(val templateId: String) : TemplateListEvent
    data object OnClickAddButton : TemplateListEvent
  }

  sealed interface TemplateListReduce : ViewModelContract.Reduce {
    data class UpdateTemplateList(val templateList: List<TemplateUIModel>) : TemplateListReduce

    data class UpdateAddDialogState(val addDialogState: AddDialogState?) : TemplateListReduce
  }

  sealed interface TemplateListSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : TemplateListSideEffect
    data class NavigateToAddTemplate(val templateName: String) : TemplateListSideEffect
    data class NavigateToEditTemplate(val templateId: String) : TemplateListSideEffect
    data class ShowSnackbar(val message: UiText) : TemplateListSideEffect
  }

  @Parcelize
  data class AddDialogState(
    val templateName: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface AddDialogEvent : TemplateListEvent {
    data class OnTemplateNameChange(val templateName: String) : AddDialogEvent
    data object OnClickCancelButton : AddDialogEvent
    data object OnClickAddButton : AddDialogEvent
  }

  sealed interface AddDialogReduce : TemplateListReduce {
    data class UpdateTemplateName(val templateName: String) : AddDialogReduce
  }
}