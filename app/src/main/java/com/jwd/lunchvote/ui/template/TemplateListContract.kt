package com.jwd.lunchvote.ui.template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.home.HomeContract
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
    object StartInitialize: TemplateListEvent
    object OnClickBackButton: TemplateListEvent
    data class OnClickTemplate(val templateId: String): TemplateListEvent
    object OnClickAddButton: TemplateListEvent
  }

  sealed interface TemplateListReduce : ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): TemplateListReduce
    data class Initialize(val state: TemplateListState): TemplateListReduce
  }

  sealed interface TemplateListSideEffect: ViewModelContract.SideEffect {
    object PopBackStack: TemplateListSideEffect
    data class NavigateToEditTemplate(val templateId: String) : TemplateListSideEffect
    object NavigateToAddTemplate: TemplateListSideEffect
    data class ShowSnackBar(val message: String) : TemplateListSideEffect
  }
}