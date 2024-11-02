package com.jwd.lunchvote.presentation.screen.template.add_template

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class AddTemplateContract {
  @Parcelize
  data class AddTemplateState(
    val name: String = "",
    val foodItemList: List<FoodItem> = emptyList(),
    val searchKeyword: String = "",
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface AddTemplateEvent : ViewModelContract.Event {
    data object ScreenInitialize : AddTemplateEvent

    data object OnClickBackButton : AddTemplateEvent
    data class OnClickFoodItem(val foodItem: FoodItem) : AddTemplateEvent
    data class OnSearchKeywordChange(val searchKeyword: String) : AddTemplateEvent
    data object OnClickAddButton : AddTemplateEvent
  }

  sealed interface AddTemplateReduce : ViewModelContract.Reduce {
    data class UpdateName(val name: String) : AddTemplateReduce
    data class UpdateFoodItemList(val foodItemList: List<FoodItem>) : AddTemplateReduce
    data class UpdateSearchKeyword(val searchKeyword: String) : AddTemplateReduce
    data class UpdateFoodStatus(val foodItem: FoodItem) : AddTemplateReduce
  }

  sealed interface AddTemplateSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : AddTemplateSideEffect
    data class ShowSnackbar(val message: UiText) : AddTemplateSideEffect
  }
}