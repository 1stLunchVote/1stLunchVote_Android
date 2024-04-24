package com.jwd.lunchvote.presentation.ui.template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Event
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Reduce
import com.jwd.lunchvote.core.ui.base.ViewModelContract.SideEffect
import com.jwd.lunchvote.core.ui.base.ViewModelContract.State
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class TemplateListContract {
  @Parcelize
  data class TemplateListState(
    val templateList: List<TemplateUIModel> = emptyList()
  ): State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TemplateListEvent: Event {
    data object OnClickBackButton: TemplateListEvent
    data class OnClickTemplate(val templateId: String): TemplateListEvent
    data object OnClickAddButton: TemplateListEvent
  }

  sealed interface TemplateListReduce : Reduce {
    data class UpdateTemplateList(val templateList: List<TemplateUIModel>): TemplateListReduce
  }

  sealed interface TemplateListSideEffect: SideEffect {
    data object PopBackStack: TemplateListSideEffect
    data class NavigateToEditTemplate(val templateId: String) : TemplateListSideEffect
    data object OpenAddDialog: TemplateListSideEffect
    data class ShowSnackBar(val message: UiText) : TemplateListSideEffect
  }
}