package com.jwd.lunchvote.presentation.ui.vote.first

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodStatus
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class FirstVoteContract {
  @Parcelize
  data class FirstVoteState(
    val user: UserUIModel = UserUIModel(),
    val lounge: LoungeUIModel = LoungeUIModel(),
    val memberList: List<MemberUIModel> = emptyList(),
    val templateList: List<TemplateUIModel> = emptyList(),
    val template: TemplateUIModel? = null,
    val foodMap: Map<FoodUIModel, FoodStatus> = emptyMap(),
    val likedFoods: List<FoodUIModel> = emptyList(),
    val dislikedFoods: List<FoodUIModel> = emptyList(),
    val searchKeyword: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface FirstVoteEvent : ViewModelContract.Event {
    data object ScreenInitialize : FirstVoteEvent

    data object OnClickBackButton : FirstVoteEvent
    data class OnSearchKeywordChange(val searchKeyword: String) : FirstVoteEvent
    data class OnClickFood(val food: FoodUIModel) : FirstVoteEvent
  }

  sealed interface FirstVoteReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : FirstVoteReduce
    data class UpdateLounge(val lounge: LoungeUIModel) : FirstVoteReduce
    data class UpdateMemberList(val memberList: List<MemberUIModel>) : FirstVoteReduce
    data class UpdateTemplateList(val templateList: List<TemplateUIModel>): FirstVoteReduce
    data class UpdateTemplate(val template: TemplateUIModel) : FirstVoteReduce
    data class UpdateFoodMap(val foodMap: Map<FoodUIModel, FoodStatus>) : FirstVoteReduce
    data class UpdateLikedFoods(val likedFoods: List<FoodUIModel>) : FirstVoteReduce
    data class UpdateDislikedFoods(val dislikedFoods: List<FoodUIModel>) : FirstVoteReduce
    data class UpdateSearchKeyword(val searchKeyword: String) : FirstVoteReduce
    data class UpdateFoodStatus(val food: FoodUIModel) : FirstVoteReduce
  }

  sealed interface FirstVoteSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FirstVoteSideEffect
    data object NavigateToSecondVote : FirstVoteSideEffect
    data object OpenTemplateDialog : FirstVoteSideEffect
    data object OpenVoteExitDialog : FirstVoteSideEffect
    data class ShowSnackBar(val message: UiText) : FirstVoteSideEffect
  }
}