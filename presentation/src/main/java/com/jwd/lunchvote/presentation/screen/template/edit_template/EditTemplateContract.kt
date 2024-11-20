package com.jwd.lunchvote.presentation.screen.template.edit_template

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class EditTemplateContract {
  @Parcelize
  data class EditTemplateState(
    val template: TemplateUIModel = TemplateUIModel(),
    val foodItemList: List<FoodItem> = emptyList(),
    val searchKeyword: String = "",

    val saveDialogState: SaveDialogState? = null,
    val deleteDialogState: DeleteDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface EditTemplateEvent : ViewModelContract.Event {
    data object ScreenInitialize : EditTemplateEvent

    data object OnClickBackButton : EditTemplateEvent
    data class OnSearchKeywordChange(val searchKeyword: String) : EditTemplateEvent
    data class OnClickFoodItem(val foodItem: FoodItem) : EditTemplateEvent
    data object OnClickSaveButton : EditTemplateEvent
    data object OnClickDeleteButton : EditTemplateEvent
  }

  sealed interface EditTemplateReduce : ViewModelContract.Reduce {
    data class UpdateTemplate(val template: TemplateUIModel) : EditTemplateReduce
    data class UpdateFoodItemList(val foodItemList: List<FoodItem>) : EditTemplateReduce
    data class UpdateSearchKeyword(val searchKeyword: String) : EditTemplateReduce
    data class UpdateFoodStatus(val foodItem: FoodItem) : EditTemplateReduce

    data class UpdateSaveDialogState(val saveDialogState: SaveDialogState?) : EditTemplateReduce
    data class UpdateDeleteDialogState(val deleteDialogState: DeleteDialogState?) : EditTemplateReduce
  }

  sealed interface EditTemplateSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : EditTemplateSideEffect
    data class ShowSnackbar(val message: UiText) : EditTemplateSideEffect
  }

  @Parcelize
  data object SaveDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SaveDialogEvent : EditTemplateEvent {
    data object OnClickCancelButton : SaveDialogEvent
    data object OnClickSaveButton : SaveDialogEvent
  }

  @Parcelize
  data object DeleteDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface DeleteDialogEvent : EditTemplateEvent {
    data object OnClickCancelButton : DeleteDialogEvent
    data object OnClickDeleteButton : DeleteDialogEvent
  }
}