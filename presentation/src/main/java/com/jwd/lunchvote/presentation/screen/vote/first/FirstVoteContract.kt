package com.jwd.lunchvote.presentation.screen.vote.first

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.FoodItem
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
    val foodItemList: List<FoodItem> = emptyList(),
    val searchKeyword: String = "",
    val finished: Boolean = false,
    val calculating: Boolean = false,

    val informationDialogState: InformationDialogState? = null,
    val exitDialogState: ExitDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface FirstVoteEvent : ViewModelContract.Event {
    data object ScreenInitialize : FirstVoteEvent

    data object OnClickBackButton : FirstVoteEvent
    data class OnSearchKeywordChange(val searchKeyword: String) : FirstVoteEvent
    data class OnClickFoodItem(val foodItem: FoodItem) : FirstVoteEvent
    data object OnClickFinishButton : FirstVoteEvent
    data object OnClickReVoteButton : FirstVoteEvent
    data object OnVoteFinish : FirstVoteEvent
  }

  sealed interface FirstVoteReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : FirstVoteReduce
    data class UpdateLounge(val lounge: LoungeUIModel) : FirstVoteReduce
    data class UpdateMemberList(val memberList: List<MemberUIModel>) : FirstVoteReduce
    data class UpdateFoodItemList(val foodItemList: List<FoodItem>) : FirstVoteReduce
    data class UpdateSearchKeyword(val searchKeyword: String) : FirstVoteReduce
    data class UpdateFoodStatus(val foodItem: FoodItem) : FirstVoteReduce
    data class UpdateFinished(val finished: Boolean) : FirstVoteReduce
    data class UpdateCalculating(val calculating: Boolean) : FirstVoteReduce

    data class UpdateInformationDialogState(val informationDialogState: InformationDialogState?) : FirstVoteReduce
    data class UpdateExitDialogState(val exitDialogState: ExitDialogState?) : FirstVoteReduce
  }

  sealed interface FirstVoteSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : FirstVoteSideEffect
    data class NavigateToSecondVote(val loungeId: String) : FirstVoteSideEffect
    data class ShowSnackbar(val message: UiText) : FirstVoteSideEffect
  }

  @Parcelize
  data class InformationDialogState(
    val templateList: List<TemplateUIModel> = emptyList(),
    val selectedTemplate: TemplateUIModel? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface InformationDialogEvent : FirstVoteEvent {
    data class OnTemplateSelected(val template: TemplateUIModel) : InformationDialogEvent
    data object OnClickSkipButton : InformationDialogEvent
    data object OnClickApplyButton : InformationDialogEvent
  }

  sealed interface InformationDialogReduce : FirstVoteReduce {
    data class UpdateTemplateList(val templateList: List<TemplateUIModel>) : InformationDialogReduce
    data class UpdateSelectedTemplate(val selectedTemplate: TemplateUIModel) : InformationDialogReduce
  }

  @Parcelize
  data object ExitDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ExitDialogEvent : FirstVoteEvent {
    data object OnClickCancelButton : ExitDialogEvent
    data object OnClickExitButton : ExitDialogEvent
  }
}