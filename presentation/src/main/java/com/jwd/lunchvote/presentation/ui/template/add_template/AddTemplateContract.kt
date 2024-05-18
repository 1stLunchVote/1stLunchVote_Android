package com.jwd.lunchvote.presentation.ui.template.add_template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.type.FoodStatus
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class AddTemplateContract {
  @Parcelize
  data class AddTemplateState(
    val name: String = "",
    val foodMap: Map<FoodUIModel, FoodStatus> = emptyMap(),
    val likedFoods: List<FoodUIModel> = emptyList(),
    val dislikedFoods: List<FoodUIModel> = emptyList(),
    val searchKeyword: String = "",
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface AddTemplateEvent : ViewModelContract.Event {
    data object ScreenInitialize : AddTemplateEvent

    data object OnClickBackButton : AddTemplateEvent
    data class OnClickFood(val food: FoodUIModel) : AddTemplateEvent
    data class OnSearchKeywordChange(val searchKeyword: String) : AddTemplateEvent
    data object OnClickAddButton : AddTemplateEvent
  }

  sealed interface AddTemplateReduce : ViewModelContract.Reduce {
    data class UpdateName(val name: String) : AddTemplateReduce
    data class UpdateFoodMap(val foodMap: Map<FoodUIModel, FoodStatus>) : AddTemplateReduce
    data class UpdateFoodStatus(val food: FoodUIModel) : AddTemplateReduce
    data class UpdateSearchKeyword(val searchKeyword: String) : AddTemplateReduce
  }

  sealed interface AddTemplateSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : AddTemplateSideEffect
    data class ShowSnackBar(val message: UiText) : AddTemplateSideEffect
  }
}