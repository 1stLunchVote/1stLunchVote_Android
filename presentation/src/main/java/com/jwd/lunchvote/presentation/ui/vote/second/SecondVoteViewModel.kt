package com.jwd.lunchvote.presentation.ui.vote.second

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.ExitLoungeUseCase
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteDialog
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteReduce
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteContract.SecondVoteState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondVoteViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val foodRepository: FoodRepository,
  private val exitLoungeUseCase: ExitLoungeUseCase,
  private val savedStateHandle: SavedStateHandle
) : BaseStateViewModel<SecondVoteState, SecondVoteEvent, SecondVoteReduce, SecondVoteSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): SecondVoteState =
    savedState as? SecondVoteState ?: SecondVoteState()

  private val _dialogState = MutableStateFlow<SecondVoteDialog?>(null)
  val dialogState: StateFlow<SecondVoteDialog?> = _dialogState.asStateFlow()
  private fun setDialogState(dialogState: SecondVoteDialog?) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  private val owner: MemberUIModel
    get() = currentState.memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner
  private val me: MemberUIModel
    get() = currentState.memberList.find { it.userId == currentState.user.id } ?: throw LoungeError.InvalidMember

  override fun handleEvents(event: SecondVoteEvent) {
    when (event) {
      is SecondVoteEvent.ScreenInitialize -> launch { initialize() }

      is SecondVoteEvent.OnClickBackButton -> setDialogState(SecondVoteDialog.ExitDialog)
      is SecondVoteEvent.OnClickFood -> {
        if (currentState.selectedFood == event.food) {
          updateState(SecondVoteReduce.UpdateSelectedFood(null))
        } else {
          updateState(SecondVoteReduce.UpdateSelectedFood(event.food))
        }
      }
      is SecondVoteEvent.OnClickFinishButton -> launch(false) { finishVote() }
      is SecondVoteEvent.OnClickReVoteButton -> launch(false) { reVote() }
      is SecondVoteEvent.OnVoteFinish -> {
        // TODO: Vote Finish
      }

      // DialogEvents
      is SecondVoteEvent.OnClickCancelButtonInExitDialog -> setDialogState(null)
      is SecondVoteEvent.OnClickConfirmButtonInExitDialog -> launch { exitVote() }
    }
  }


  override fun reduceState(state: SecondVoteState, reduce: SecondVoteReduce): SecondVoteState {
    return when (reduce) {
      is SecondVoteReduce.UpdateUser -> state.copy(user = reduce.user)
      is SecondVoteReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is SecondVoteReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
      is SecondVoteReduce.UpdateFoodList -> state.copy(foodList = reduce.foodList)
      is SecondVoteReduce.UpdateSelectedFood -> state.copy(selectedFood = reduce.food)
      is SecondVoteReduce.UpdateFinished -> state.copy(finished = reduce.finished)
      is SecondVoteReduce.UpdateCalculating -> state.copy(calculating = reduce.calculating)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(SecondVoteSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize() {
    // TODO: Initialize
  }

  private suspend fun finishVote() {
    memberRepository.updateMemberStatus(me.asDomain(), MemberUIModel.Status.VOTED.asDomain())

    updateState(SecondVoteReduce.UpdateFinished(true))
  }

  private suspend fun reVote() {
    memberRepository.updateMemberStatus(me.asDomain(), MemberUIModel.Status.VOTING.asDomain())

    updateState(SecondVoteReduce.UpdateFinished(false))
  }

  private suspend fun exitVote() {
    exitLoungeUseCase(me.asDomain())

    sendSideEffect(SecondVoteSideEffect.PopBackStack)
  }
}