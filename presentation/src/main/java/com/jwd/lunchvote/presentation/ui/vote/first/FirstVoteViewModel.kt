package com.jwd.lunchvote.presentation.ui.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.common.error.UserError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.CalculateFirstVoteResult
import com.jwd.lunchvote.domain.usecase.StartSecondVoteUseCase
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodStatus
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class FirstVoteViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val foodRepository: FoodRepository,
  private val templateRepository: TemplateRepository,
  private val firstVoteRepository: FirstVoteRepository,
  private val calculateFirstVoteResult: CalculateFirstVoteResult,
  private val startSecondVoteUseCase: StartSecondVoteUseCase,
  private val savedStateHandle: SavedStateHandle
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): FirstVoteState =
    savedState as? FirstVoteState ?: FirstVoteState()

  private val owner: MemberUIModel
    get() = currentState.memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner
  private val me: MemberUIModel
    get() = currentState.memberList.find { it.userId == currentState.user.id } ?: throw LoungeError.InvalidMember

  override fun handleEvents(event: FirstVoteEvent) {
    when(event) {
      is FirstVoteEvent.ScreenInitialize -> launch { initialize() }

      is FirstVoteEvent.OnClickBackButton -> sendSideEffect(FirstVoteSideEffect.PopBackStack)
      is FirstVoteEvent.OnSearchKeywordChange -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
      is FirstVoteEvent.OnClickFood -> updateState(FirstVoteReduce.UpdateFoodStatus(event.food))
      is FirstVoteEvent.OnClickFinishButton -> launch(false) { finishVote() }
      is FirstVoteEvent.OnClickReVoteButton -> launch(false) { reVote() }
      is FirstVoteEvent.OnVoteFinish -> launch { submitVote() }
    }
  }

  override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
    return when(reduce) {
      is FirstVoteReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is FirstVoteReduce.UpdateUser -> state.copy(user = reduce.user)
      is FirstVoteReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
      is FirstVoteReduce.UpdateTemplateList -> state.copy(templateList = reduce.templateList)
      is FirstVoteReduce.UpdateTemplate -> state.copy(template = reduce.template)
      is FirstVoteReduce.UpdateFoodMap -> state.copy(foodMap = reduce.foodMap)
      is FirstVoteReduce.UpdateLikedFoods -> state.copy(likedFoods = reduce.likedFoods)
      is FirstVoteReduce.UpdateDislikedFoods -> state.copy(dislikedFoods = reduce.dislikedFoods)
      is FirstVoteReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is FirstVoteReduce.UpdateFoodStatus -> when (reduce.food) {
        in state.likedFoods -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          likedFoods = state.likedFoods.filter { it.id != reduce.food.id },
          dislikedFoods = state.dislikedFoods + reduce.food
        )
        in state.dislikedFoods -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          dislikedFoods = state.dislikedFoods.filter { it.id != reduce.food.id }
        )
        else -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          likedFoods = state.likedFoods + reduce.food
        )
      }
      is FirstVoteReduce.UpdateFinished -> state.copy(finished = reduce.finished)
      is FirstVoteReduce.UpdateCalculating -> state.copy(calculating = reduce.calculating)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(FirstVoteSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize() {
    val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser
    val user = userRepository.getUserById(userId).asUI()
    updateState(FirstVoteReduce.UpdateUser(user))

    val loungeIdKey = LunchVoteNavRoute.FirstVote.arguments.first().name
    val loungeId = checkNotNull(savedStateHandle.get<String>(loungeIdKey))
    val lounge = loungeRepository.getLoungeById(loungeId).asUI()
    updateState(FirstVoteReduce.UpdateLounge(lounge))

    launch(false) { collectMemberList(loungeId) }
    launch(false) { collectLoungeStatus(loungeId) }

    val templateList = templateRepository.getTemplateList(userId).map { it.asUI() }
    updateState(FirstVoteReduce.UpdateTemplateList(templateList))

    val foodList = foodRepository.getAllFood().map { it.asUI() }
    val foodMap = foodList.associateWith { FoodStatus.DEFAULT }
    updateState(FirstVoteReduce.UpdateFoodMap(foodMap))

//    sendSideEffect(FirstVoteSideEffect.OpenTemplateDialog)
  }

  private suspend fun collectMemberList(loungeId: String) {
    memberRepository.getMemberListFlow(loungeId).collectLatest { memberList ->
      updateState(FirstVoteReduce.UpdateMemberList(memberList.map { it.asUI() }))

      if (memberList.all { it.status == Member.Status.VOTED }) launch { submitVote() }
    }
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatusFlowById(loungeId).collectLatest { status ->
      if (status == Lounge.Status.SECOND_VOTE) sendSideEffect(FirstVoteSideEffect.NavigateToSecondVote)
    }
  }

  private suspend fun selectTemplate(template: TemplateUIModel) {
    updateState(FirstVoteReduce.UpdateTemplate(template))

    val foodList = foodRepository.getAllFood().map { it.asUI() }
    val foodMap = foodList.associateWith {
      when (it.name) {
        in template.likedFoodIds -> FoodStatus.LIKE
        in template.dislikedFoodIds -> FoodStatus.DISLIKE
        else -> FoodStatus.DEFAULT
      }
    }
    val likeList = foodList.filter { template.likedFoodIds.contains(it.name) }
    val dislikeList = foodList.filter { template.dislikedFoodIds.contains(it.name) }

    updateState(FirstVoteReduce.UpdateFoodMap(foodMap))
    updateState(FirstVoteReduce.UpdateLikedFoods(likeList))
    updateState(FirstVoteReduce.UpdateDislikedFoods(dislikeList))
  }

  private suspend fun finishVote() {
    memberRepository.updateMemberStatus(me.asDomain(), MemberUIModel.Status.VOTED.asDomain())

    updateState(FirstVoteReduce.UpdateFinished(true))
  }

  private suspend fun reVote() {
    memberRepository.updateMemberStatus(me.asDomain(), MemberUIModel.Status.VOTING.asDomain())

    updateState(FirstVoteReduce.UpdateFinished(false))
  }

  private suspend fun submitVote() {
    updateState(FirstVoteReduce.UpdateCalculating(true))

    firstVoteRepository.submitVote(
      loungeId = currentState.lounge.id,
      userId = me.userId,
      likedFoodIds = currentState.likedFoods.map { it.id },
      dislikedFoodIds = currentState.dislikedFoods.map { it.id }
    )

    if (me.userId == owner.userId) {
      val selectedFoodIds = calculateFirstVoteResult(currentState.lounge.id)
      // TODO: 투표 결과 활용하기

      startSecondVoteUseCase(currentState.lounge.id)
    }
  }
}