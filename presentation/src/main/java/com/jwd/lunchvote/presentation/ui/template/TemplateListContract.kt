package com.jwd.lunchvote.presentation.ui.template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract.DialogState
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Event
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Reduce
import com.jwd.lunchvote.core.ui.base.ViewModelContract.SideEffect
import com.jwd.lunchvote.core.ui.base.ViewModelContract.State
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import kotlinx.parcelize.Parcelize

class TemplateListContract {
  @Parcelize
  data class TemplateListState(
    val loading: Boolean = false,
    val templateList: List<TemplateUIModel> = emptyList()
  ): State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TemplateListEvent: Event {
    data object StartInitialize: TemplateListEvent
    data object OnClickBackButton: TemplateListEvent
    data class OnClickTemplate(val templateId: String): TemplateListEvent
    data object OnClickAddButton: TemplateListEvent
    data object OnClickDismissButton: TemplateListEvent
  }

  sealed interface TemplateListReduce : Reduce {
    data class UpdateLoading(val loading: Boolean): TemplateListReduce
    data class Initialize(val state: TemplateListState): TemplateListReduce
  }

  sealed interface TemplateListSideEffect: SideEffect {
    data object PopBackStack: TemplateListSideEffect
    data class NavigateToEditTemplate(val templateId: String) : TemplateListSideEffect
    data class NavigateToAddTemplate(val templateName: String): TemplateListSideEffect
    data class ShowSnackBar(val message: String) : TemplateListSideEffect
  }

  sealed interface TemplateListDialogState: DialogState {
    data class AddTemplate(val onClickConfirm: (String) -> Unit): TemplateListDialogState
  }
}