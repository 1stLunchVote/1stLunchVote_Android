package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Event
import com.jwd.lunchvote.core.ui.base.ViewModelContract.Reduce
import com.jwd.lunchvote.core.ui.base.ViewModelContract.SideEffect
import com.jwd.lunchvote.core.ui.base.ViewModelContract.State
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class EditTemplateContract {
  @Parcelize
  data class EditTemplateState(
    val template: TemplateUIModel = TemplateUIModel(),
    val foodItemList: List<FoodItem> = emptyList(),
    val searchKeyword: String = ""
  ) : State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface EditTemplateEvent : Event {
    data object ScreenInitialize : EditTemplateEvent

    data object OnClickBackButton : EditTemplateEvent
    data class OnSearchKeywordChange(val searchKeyword: String) : EditTemplateEvent
    data class OnClickFoodItem(val foodItem: FoodItem) : EditTemplateEvent
    data object OnClickSaveButton : EditTemplateEvent
    data object OnClickDeleteButton : EditTemplateEvent

    // DialogEvent
    data object OnClickCancelButtonConfirmDialog : EditTemplateEvent
    data object OnClickConfirmButtonConfirmDialog : EditTemplateEvent
    data object OnClickCancelButtonDeleteDialog : EditTemplateEvent
    data object OnClickDeleteButtonDeleteDialog : EditTemplateEvent
  }

  sealed interface EditTemplateReduce : Reduce {
    data class UpdateTemplate(val template: TemplateUIModel) : EditTemplateReduce
    data class UpdateFoodItemList(val foodItemList: List<FoodItem>) : EditTemplateReduce
    data class UpdateSearchKeyword(val searchKeyword: String) : EditTemplateReduce
    data class UpdateFoodStatus(val foodItem: FoodItem) : EditTemplateReduce
  }

  sealed interface EditTemplateSideEffect : SideEffect {
    data object PopBackStack : EditTemplateSideEffect
    data object OpenDeleteDialog : EditTemplateSideEffect
    data object OpenConfirmDialog : EditTemplateSideEffect
    data object CloseDialog : EditTemplateSideEffect
    data class ShowSnackbar(val message: UiText) : EditTemplateSideEffect
  }

  companion object {
    const val CONFIRM_DIALOG = "confirm_dialog"
    const val DELETE_DIALOG = "delete_dialog"
  }
}