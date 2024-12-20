package com.jwd.lunchvote.presentation.screen.vote.second

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.BallotRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import com.jwd.lunchvote.domain.usecase.CalculateSecondVote
import com.jwd.lunchvote.domain.usecase.ExitLounge
import com.jwd.lunchvote.domain.usecase.FinishVote
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.SecondBallotUIModel
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.ExitDialogEvent
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.ExitDialogState
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteEvent
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteReduce
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteSideEffect
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.error.LoungeError
import kr.co.inbody.config.error.MemberError
import kr.co.inbody.config.error.RouteError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class SecondVoteViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val userStatusRepository: UserStatusRepository,
  private val memberRepository: MemberRepository,
  private val voteResultRepository: VoteResultRepository,
  private val foodRepository: FoodRepository,
  private val ballotRepository: BallotRepository,
  private val calculateSecondVote: CalculateSecondVote,
  private val finishVote: FinishVote,
  private val exitLounge: ExitLounge,
  savedStateHandle: SavedStateHandle
) : BaseStateViewModel<SecondVoteState, SecondVoteEvent, SecondVoteReduce, SecondVoteSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): SecondVoteState {
    return savedState as? SecondVoteState ?: SecondVoteState()
  }

  private val loungeId: String =
    savedStateHandle[LunchVoteNavRoute.SecondVote.arguments.first().name] ?: throw RouteError.NoArguments

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  private lateinit var loungeStatusFlow: Job
  private lateinit var memberListFlow: Job

  private fun getOwner(memberList: List<MemberUIModel> = currentState.memberList): MemberUIModel =
    memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner

  override fun handleEvents(event: SecondVoteEvent) {
    when (event) {
      is SecondVoteEvent.ScreenInitialize -> launch { initialize() }

      is SecondVoteEvent.OnClickBackButton -> updateState(SecondVoteReduce.UpdateExitDialogState(ExitDialogState))
      is SecondVoteEvent.OnClickFood -> {
        if (currentState.selectedFood == event.food) {
          updateState(SecondVoteReduce.UpdateSelectedFood(null))
        } else {
          updateState(SecondVoteReduce.UpdateSelectedFood(event.food))
        }
      }
      is SecondVoteEvent.OnClickFinishButton -> launch(false) { finishVote() }
      is SecondVoteEvent.OnClickReVoteButton -> launch(false) { reVote() }
      is SecondVoteEvent.OnVoteFinish -> launch { submitVote() }

      is ExitDialogEvent -> handleExitDialogEvents(event)
    }
  }

  private fun handleExitDialogEvents(event: ExitDialogEvent) {
    when (event) {
      is ExitDialogEvent.OnClickCancelButton -> updateState(SecondVoteReduce.UpdateExitDialogState(null))
      is ExitDialogEvent.OnClickExitButton -> launch { exitVote() }
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
      is SecondVoteReduce.UpdateExitDialogState -> state.copy(exitDialogState = reduce.exitDialogState)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(SecondVoteSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val user = userRepository.getUserById(userId).asUI()
    updateState(SecondVoteReduce.UpdateUser(user))

    val lounge = loungeRepository.getLoungeById(loungeId).asUI()
    updateState(SecondVoteReduce.UpdateLounge(lounge))

    loungeStatusFlow = launch { collectLoungeStatus(lounge.id) }
    memberListFlow = launch { collectMemberList(lounge.id) }

    val firstVoteResult = voteResultRepository.getFirstVoteResultByLoungeId(loungeId)
    val foodList = firstVoteResult.foodIds.map { id -> foodRepository.getFoodById(id).asUI() }
    updateState(SecondVoteReduce.UpdateFoodList(foodList))
  }

  private suspend fun collectMemberList(loungeId: String) {
    memberRepository.getMemberListFlow(loungeId).collectLatest { memberList ->
      updateState(SecondVoteReduce.UpdateMemberList(memberList.map { it.asUI() }))

      if (memberList.size <= 1) {
        sendSideEffect(SecondVoteSideEffect.ShowSnackbar(UiText.StringResource(R.string.first_vote_only_owner_snackbar)))
        sendSideEffect(SecondVoteSideEffect.PopBackStack)
      }
      if (memberList.all { it.status == Member.Status.VOTED }) launch { submitVote() }
    }
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatusFlowById(loungeId).collectLatest { status ->
      when(status) {
        Lounge.Status.QUIT -> {
          userStatusRepository.setUserLounge(userId, null)

          sendSideEffect(SecondVoteSideEffect.ShowSnackbar(UiText.StringResource(R.string.first_vote_owner_exited_snackbar)))
          sendSideEffect(SecondVoteSideEffect.PopBackStack)
        }
        Lounge.Status.FINISHED -> sendSideEffect(SecondVoteSideEffect.NavigateToVoteResult(currentState.lounge.id))
        else -> Unit
      }
    }
  }

  private suspend fun finishVote() {
    val me = memberRepository.getMember(userId, currentState.lounge.id)?.asUI() ?: throw MemberError.InvalidMember
    memberRepository.updateMemberStatus(me.asDomain(), MemberUIModel.Status.VOTED.asDomain())

    updateState(SecondVoteReduce.UpdateFinished(true))
  }

  private suspend fun reVote() {
    val me = memberRepository.getMember(userId, currentState.lounge.id)?.asUI() ?: throw MemberError.InvalidMember
    memberRepository.updateMemberStatus(me.asDomain(), MemberUIModel.Status.VOTING.asDomain())

    updateState(SecondVoteReduce.UpdateFinished(false))
  }

  private suspend fun submitVote() {
    updateState(SecondVoteReduce.UpdateCalculating(true))

    val owner = getOwner()

    val ballot = SecondBallotUIModel(
      loungeId = currentState.lounge.id,
      userId = currentState.user.id,
      foodId = (currentState.selectedFood ?: currentState.foodList.random()).id
    )
    ballotRepository.submitSecondBallot(ballot.asDomain())

    if (userId == owner.userId) {
      calculateSecondVote(currentState.lounge.id)
      currentState.memberList.forEach {
        userStatusRepository.setUserLounge(it.userId, null)
      }
      finishVote(currentState.lounge.id)
    }
  }

  private suspend fun exitVote() {
    currentState.exitDialogState ?: return
    updateState(SecondVoteReduce.UpdateExitDialogState(null))

    loungeStatusFlow.cancel()
    memberListFlow.cancel()

    userStatusRepository.setUserLounge(userId, null)
    exitLounge(userId)

    sendSideEffect(SecondVoteSideEffect.PopBackStack)
  }
}