package com.jwd.lunchvote.ui.template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.TemplateUIModel
import kotlinx.parcelize.Parcelize

class TemplateListContract {
  @Parcelize
  data class TemplateListState(
    val loading: Boolean = false,
    val templateList: List<TemplateUIModel> = emptyList()
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TemplateListEvent: ViewModelContract.Event {
    data object StartInitialize: TemplateListEvent
    data object OnClickBackButton: TemplateListEvent
    data class OnClickTemplate(val templateId: String): TemplateListEvent
    data object OnClickAddButton: TemplateListEvent
    data object OnClickDismissButton: TemplateListEvent
  }

  sealed interface TemplateListReduce : ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): TemplateListReduce
    data class Initialize(val state: TemplateListState): TemplateListReduce
  }

  sealed interface TemplateListSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack: TemplateListSideEffect
    data class NavigateToEditTemplate(val templateId: String) : TemplateListSideEffect
    data class NavigateToAddTemplate(val templateName: String): TemplateListSideEffect
    data class ShowSnackBar(val message: String) : TemplateListSideEffect
  }

  sealed interface TemplateListDialogState: ViewModelContract.DialogState {
    data class AddTemplate(val onClickConfirm: (String) -> Unit): TemplateListDialogState
  }
}