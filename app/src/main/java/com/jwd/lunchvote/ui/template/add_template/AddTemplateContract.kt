package com.jwd.lunchvote.ui.template.add_template

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract
import kotlinx.parcelize.Parcelize

class AddTemplateContract {
  @Parcelize
  data class AddTemplateState(
    val loading: Boolean = false,
    val template: TemplateUIModel = TemplateUIModel("", "", emptyList(), emptyList()),
    val foodList: List<FoodUIModel> = emptyList(),
    val likeList: List<FoodUIModel> = emptyList(),
    val dislikeList: List<FoodUIModel> = emptyList(),
    val searchKeyword: String = "",
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface AddTemplateEvent: ViewModelContract.Event {
    data class StartInitialize(val templateName: String): AddTemplateEvent
    object OnClickBackButton: AddTemplateEvent
    data class OnClickFood(val food: FoodUIModel): AddTemplateEvent
    data class SetSearchKeyword(val searchKeyword: String): AddTemplateEvent
    object OnClickAddButton: AddTemplateEvent
  }

  sealed interface AddTemplateReduce : ViewModelContract.Reduce {
    data class UpdateLoading(val loading: Boolean): AddTemplateReduce
    data class Initialize(val state: AddTemplateState): AddTemplateReduce
    data class UpdateFoodStatus(val food: FoodUIModel): AddTemplateReduce
    data class UpdateSearchKeyword(val searchKeyword: String): AddTemplateReduce
  }

  sealed interface AddTemplateSideEffect: ViewModelContract.SideEffect {
    data class PopBackStack(val message: String = ""): AddTemplateSideEffect
    data class ShowSnackBar(val message: String) : AddTemplateSideEffect
  }
}