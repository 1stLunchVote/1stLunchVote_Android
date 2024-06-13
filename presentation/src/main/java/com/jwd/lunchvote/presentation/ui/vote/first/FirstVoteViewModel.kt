package com.jwd.lunchvote.presentation.ui.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.jwd.lunchvote.domain.repository.SecondVoteRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.CalculateFirstVoteResult
import com.jwd.lunchvote.domain.usecase.ExitLoungeUseCase
import com.jwd.lunchvote.domain.usecase.StartSecondVoteUseCase
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodStatus
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteDialog
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstVoteViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val foodRepository: FoodRepository,
  private val templateRepository: TemplateRepository,
  private val firstVoteRepository: FirstVoteRepository,
  private val secondVoteRepository: SecondVoteRepository,
  private val calculateFirstVoteResult: CalculateFirstVoteResult,
  private val startSecondVoteUseCase: StartSecondVoteUseCase,
  private val exitLoungeUseCase: ExitLoungeUseCase,
  private val savedStateHandle: SavedStateHandle
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): FirstVoteState =
    savedState as? FirstVoteState ?: FirstVoteState()

  private val _dialogState = MutableStateFlow<FirstVoteDialog?>(null)
  val dialogState: StateFlow<FirstVoteDialog?> = _dialogState.asStateFlow()
  private fun setDialogState(dialogState: FirstVoteDialog?) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  private val owner: MemberUIModel
    get() = currentState.memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner
  private val me: MemberUIModel
    get() = currentState.memberList.find { it.userId == currentState.user.id } ?: throw LoungeError.InvalidMember

  override fun handleEvents(event: FirstVoteEvent) {
    when(event) {
      is FirstVoteEvent.ScreenInitialize -> launch { initialize() }

      is FirstVoteEvent.OnClickBackButton -> setDialogState(FirstVoteDialog.ExitDialog)
      is FirstVoteEvent.OnSearchKeywordChange -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
      is FirstVoteEvent.OnClickFood -> updateState(FirstVoteReduce.UpdateFoodStatus(event.food))
      is FirstVoteEvent.OnClickFinishButton -> launch(false) { finishVote() }
      is FirstVoteEvent.OnClickReVoteButton -> launch(false) { reVote() }
      is FirstVoteEvent.OnVoteFinish -> launch { submitVote() }

      // DialogEvents
      is FirstVoteEvent.OnClickCancelButtonInSelectTemplateDialog -> setDialogState(null)
      is FirstVoteEvent.OnTemplateChangeInSelectTemplateDialog -> updateState(FirstVoteReduce.UpdateTemplate(event.template))
      is FirstVoteEvent.OnClickApplyButtonInSelectTemplateDialog -> launch { selectTemplate(currentState.template) }
      is FirstVoteEvent.OnClickCancelButtonInExitDialog -> setDialogState(null)
      is FirstVoteEvent.OnClickConfirmButtonInExitDialog -> launch { exitVote() }
    }
  }

  override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
    return when(reduce) {
      is FirstVoteReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is FirstVoteReduce.UpdateUser -> state.copy(user = reduce.user)
      is FirstVoteReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
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

      is FirstVoteReduce.UpdateTemplate -> state.copy(template = reduce.template)
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

    val foodList = foodRepository.getAllFood().map { it.asUI() }
    val foodMap = foodList.associateWith { FoodStatus.DEFAULT }
    updateState(FirstVoteReduce.UpdateFoodMap(foodMap))

    val templateList = templateRepository.getTemplateList(userId).map { it.asUI() }

    setDialogState(FirstVoteDialog.SelectTemplateDialog(templateList))
  }

  private suspend fun collectMemberList(loungeId: String) {
    memberRepository.getMemberListFlow(loungeId).collectLatest { memberList ->
      updateState(FirstVoteReduce.UpdateMemberList(memberList.map { it.asUI() }))

      if (memberList.size <= 1) {
        sendSideEffect(FirstVoteSideEffect.ShowSnackBar(UiText.StringResource(R.string.first_vote_only_owner_snackbar)))
        sendSideEffect(FirstVoteSideEffect.PopBackStack)
      }
      if (memberList.all { it.status == Member.Status.VOTED }) launch { submitVote() }
    }
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatusFlowById(loungeId).collectLatest { status ->
      when(status) {
        Lounge.Status.QUIT -> {
          sendSideEffect(FirstVoteSideEffect.ShowSnackBar(UiText.StringResource(R.string.first_vote_owner_exited_snackbar)))
          sendSideEffect(FirstVoteSideEffect.PopBackStack)
        }
        Lounge.Status.SECOND_VOTE -> sendSideEffect(FirstVoteSideEffect.NavigateToSecondVote(currentState.lounge.id))
        else -> Unit
      }
    }
  }

  private suspend fun selectTemplate(template: TemplateUIModel?) {
    val foodList = foodRepository.getAllFood().map { it.asUI() }

    if (template == null) {
      val foodMap = foodList.associateWith { FoodStatus.DEFAULT }

      updateState(FirstVoteReduce.UpdateFoodMap(foodMap))
    } else {
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
      secondVoteRepository.createSecondVote(currentState.lounge.id, selectedFoodIds)

      startSecondVoteUseCase(currentState.lounge.id)
    }
  }

  private suspend fun exitVote() {
    exitLoungeUseCase(me.asDomain())

    sendSideEffect(FirstVoteSideEffect.PopBackStack)
  }
}