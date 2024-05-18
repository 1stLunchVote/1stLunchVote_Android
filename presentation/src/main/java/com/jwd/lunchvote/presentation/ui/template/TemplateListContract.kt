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
    val templateList: List<TemplateUIModel> = emptyList(),
    val templateName: String? = null
  ) : State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TemplateListEvent : Event {
    data object ScreenInitialize : TemplateListEvent

    data object OnClickBackButton : TemplateListEvent
    data class OnClickTemplate(val templateId: String) : TemplateListEvent
    data object OnClickAddButton : TemplateListEvent

    // DialogEvents
    data class OnTemplateNameChange(val templateName: String) : TemplateListEvent
    data object OnClickDismissButtonAddDialog : TemplateListEvent
    data object OnClickConfirmButtonAddDialog : TemplateListEvent
  }

  sealed interface TemplateListReduce : Reduce {
    data class UpdateTemplateList(val templateList: List<TemplateUIModel>) : TemplateListReduce
    data class UpdateTemplateName(val templateName: String?) : TemplateListReduce
  }

  sealed interface TemplateListSideEffect : SideEffect {
    data object PopBackStack : TemplateListSideEffect
    data class NavigateToAddTemplate(val templateName: String) : TemplateListSideEffect
    data class NavigateToEditTemplate(val templateId: String) : TemplateListSideEffect
    data object OpenAddDialog : TemplateListSideEffect
    data object CloseDialog : TemplateListSideEffect
    data class ShowSnackBar(val message: UiText) : TemplateListSideEffect
  }

  companion object {
    const val ADD_DIALOG = "add_dialog"
  }
}