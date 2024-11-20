package com.jwd.lunchvote.presentation.screen.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.repository.BallotRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.domain.usecase.CalculateFirstVote
import com.jwd.lunchvote.domain.usecase.ExitLounge
import com.jwd.lunchvote.domain.usecase.StartSecondVote
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FirstBallotUIModel
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.ExitDialogEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.ExitDialogState
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.InformationDialogEvent
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.InformationDialogReduce
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteContract.InformationDialogState
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteContract.SecondVoteReduce
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
class FirstVoteViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val userStatusRepository: UserStatusRepository,
  private val memberRepository: MemberRepository,
  private val foodRepository: FoodRepository,
  private val templateRepository: TemplateRepository,
  private val ballotRepository: BallotRepository,
  private val calculateFirstVote: CalculateFirstVote,
  private val startSecondVote: StartSecondVote,
  private val exitLounge: ExitLounge,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): FirstVoteState {
    return savedState as? FirstVoteState ?: FirstVoteState()
  }

  private val loungeId: String =
    savedStateHandle[LunchVoteNavRoute.FirstVote.arguments.first().name] ?: throw RouteError.NoArguments

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  private lateinit var loungeStatusFlow: Job
  private lateinit var memberListFlow: Job

  private fun getOwner(memberList: List<MemberUIModel> = currentState.memberList): MemberUIModel =
    memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner

  override fun handleEvents(event: FirstVoteEvent) {
    when(event) {
      is FirstVoteEvent.ScreenInitialize -> launch { initialize() }

      is FirstVoteEvent.OnClickBackButton -> updateState(FirstVoteReduce.UpdateExitDialogState(ExitDialogState))
      is FirstVoteEvent.OnSearchKeywordChange -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
      is FirstVoteEvent.OnClickFoodItem -> updateState(FirstVoteReduce.UpdateFoodStatus(event.foodItem))
      is FirstVoteEvent.OnClickFinishButton -> launch(false) { finishVote() }
      is FirstVoteEvent.OnClickReVoteButton -> launch(false) { reVote() }
      is FirstVoteEvent.OnVoteFinish -> launch { submitVote() }

      is InformationDialogEvent -> handleInformationDialogEvents(event)
      is ExitDialogEvent -> handleExitDialogEvents(event)
    }
  }

  private fun handleInformationDialogEvents(event: InformationDialogEvent) {
    when(event) {
      is InformationDialogEvent.OnTemplateSelected -> updateState(InformationDialogReduce.UpdateSelectedTemplate(event.template))
      is InformationDialogEvent.OnClickSkipButton -> updateState(FirstVoteReduce.UpdateInformationDialogState(null))
      is InformationDialogEvent.OnClickApplyButton -> launch { selectTemplate() }
    }
  }

  private fun handleExitDialogEvents(event: ExitDialogEvent) {
    when(event) {
      is ExitDialogEvent.OnClickCancelButton -> updateState(FirstVoteReduce.UpdateExitDialogState(null))
      is ExitDialogEvent.OnClickExitButton -> launch { exitVote() }
    }
  }

  override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
    return when(reduce) {
      is FirstVoteReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is FirstVoteReduce.UpdateUser -> state.copy(user = reduce.user)
      is FirstVoteReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
      is FirstVoteReduce.UpdateFoodItemList -> state.copy(foodItemList = reduce.foodItemList)
      is FirstVoteReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is FirstVoteReduce.UpdateFoodStatus -> state.copy(
        foodItemList = state.foodItemList.map { if (it == reduce.foodItem) it.nextStatus() else it }
      )
      is FirstVoteReduce.UpdateFinished -> state.copy(finished = reduce.finished)
      is FirstVoteReduce.UpdateCalculating -> state.copy(calculating = reduce.calculating)
      is FirstVoteReduce.UpdateInformationDialogState -> state.copy(informationDialogState = reduce.informationDialogState)
      is FirstVoteReduce.UpdateExitDialogState -> state.copy(exitDialogState = reduce.exitDialogState)

      is InformationDialogReduce -> state.copy(informationDialogState = reduceInformationDialogState(state.informationDialogState, reduce))
    }
  }

  private fun reduceInformationDialogState(state: InformationDialogState?, reduce: InformationDialogReduce): InformationDialogState? {
    return when(reduce) {
      is InformationDialogReduce.UpdateTemplateList -> state?.copy(templateList = reduce.templateList)
      is InformationDialogReduce.UpdateSelectedTemplate -> state?.copy(selectedTemplate = reduce.selectedTemplate)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(FirstVoteSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val user = userRepository.getUserById(userId).asUI()
    updateState(FirstVoteReduce.UpdateUser(user))

    val lounge = loungeRepository.getLoungeById(loungeId).asUI()
    updateState(FirstVoteReduce.UpdateLounge(lounge))

    loungeStatusFlow = launch { collectLoungeStatus(lounge.id) }
    memberListFlow = launch { collectMemberList(lounge.id) }

    val templateList = templateRepository.getTemplateList(userId).map { it.asUI() }

    updateState(FirstVoteReduce.UpdateInformationDialogState(InformationDialogState()))
    updateState(InformationDialogReduce.UpdateTemplateList(templateList))

    val foodItemList = foodRepository.getAllFood().map { food -> FoodItem(food = food.asUI()) }
    updateState(FirstVoteReduce.UpdateFoodItemList(foodItemList))
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatusFlowById(loungeId).collectLatest { status ->
      when(status) {
        Lounge.Status.QUIT -> {
          userStatusRepository.setUserLounge(userId, null)

          sendSideEffect(FirstVoteSideEffect.ShowSnackbar(UiText.StringResource(R.string.first_vote_owner_exited_snackbar)))
          sendSideEffect(FirstVoteSideEffect.PopBackStack)
        }
        Lounge.Status.SECOND_VOTE -> sendSideEffect(FirstVoteSideEffect.NavigateToSecondVote(currentState.lounge.id))
        else -> Unit
      }
    }
  }

  private suspend fun collectMemberList(loungeId: String) {
    memberRepository.getMemberListFlow(loungeId).collectLatest { members ->
      val memberList = members.map { it.asUI() }
      updateState(FirstVoteReduce.UpdateMemberList(memberList))

      if (memberList.size <= 1) {
        userStatusRepository.setUserLounge(userId, null)

        sendSideEffect(FirstVoteSideEffect.ShowSnackbar(UiText.StringResource(R.string.first_vote_only_owner_snackbar)))
        sendSideEffect(FirstVoteSideEffect.PopBackStack)
      }
      if (memberList.all { it.status == MemberUIModel.Status.VOTED }) launch { submitVote() }
    }
  }

  private suspend fun selectTemplate() {
    val dialogState = currentState.informationDialogState ?: return
    updateState(FirstVoteReduce.UpdateInformationDialogState(null))

    dialogState.selectedTemplate?.let { template ->
      val foodItemList = foodRepository.getAllFood().map { food ->
        FoodItem(
          food = food.asUI(),
          status = when(food.id) {
            in template.likedFoodIds -> FoodItem.Status.LIKE
            in template.dislikedFoodIds -> FoodItem.Status.DISLIKE
            else -> FoodItem.Status.DEFAULT
          }
        )
      }

      updateState(FirstVoteReduce.UpdateFoodItemList(foodItemList))
    }
  }

  private suspend fun finishVote() {
    val me = memberRepository.getMember(userId, currentState.lounge.id) ?: throw MemberError.InvalidMember
    memberRepository.updateMemberStatus(me, MemberUIModel.Status.VOTED.asDomain())

    updateState(FirstVoteReduce.UpdateFinished(true))
  }

  private suspend fun reVote() {
    val me = memberRepository.getMember(userId, currentState.lounge.id) ?: throw MemberError.InvalidMember
    memberRepository.updateMemberStatus(me, MemberUIModel.Status.VOTING.asDomain())

    updateState(FirstVoteReduce.UpdateFinished(false))
  }

  private suspend fun submitVote() {
    updateState(FirstVoteReduce.UpdateCalculating(true))

    val owner = getOwner()

    val likedFoodsId = currentState.foodItemList.filter { it.status == FoodItem.Status.LIKE }.map { it.food.id }
    val dislikedFoodsId = currentState.foodItemList.filter { it.status == FoodItem.Status.DISLIKE }.map { it.food.id }
    val ballot = FirstBallotUIModel(
      loungeId = currentState.lounge.id,
      userId = userId,
      likedFoodIds = likedFoodsId,
      dislikedFoodIds = dislikedFoodsId
    )
    ballotRepository.submitFirstBallot(ballot.asDomain())

    if (userId == owner.userId) {
      calculateFirstVote(currentState.lounge.id)
      startSecondVote(currentState.lounge.id)
    }
  }

  private suspend fun exitVote() {
    currentState.exitDialogState ?: return
    updateState(FirstVoteReduce.UpdateExitDialogState(null))

    loungeStatusFlow.cancel()
    memberListFlow.cancel()

    userStatusRepository.setUserLounge(userId, null)
    exitLounge(userId)

    sendSideEffect(FirstVoteSideEffect.PopBackStack)
  }
}