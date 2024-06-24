package com.jwd.lunchvote.presentation.ui.vote.first

import android.os.Parcelable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.BallotRepository
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.StorageRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.CalculateFirstVote
import com.jwd.lunchvote.domain.usecase.ExitLounge
import com.jwd.lunchvote.domain.usecase.StartSecondVote
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FirstBallotUIModel
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteDialog
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.LoungeError
import kr.co.inbody.config.error.MemberError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class FirstVoteViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val foodRepository: FoodRepository,
  private val storageRepository: StorageRepository,
  private val templateRepository: TemplateRepository,
  private val ballotRepository: BallotRepository,
  private val calculateFirstVote: CalculateFirstVote,
  private val startSecondVote: StartSecondVote,
  private val exitLounge: ExitLounge,
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

  private lateinit var loungeStatusFlow: Job
  private lateinit var memberListFlow: Job

  private val owner: MemberUIModel
    get() = currentState.memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner
  private val me: MemberUIModel
    get() = currentState.memberList.find { it.userId == currentState.user.id } ?: throw MemberError.InvalidMember

  override fun handleEvents(event: FirstVoteEvent) {
    when(event) {
      is FirstVoteEvent.ScreenInitialize -> launch { initialize() }

      is FirstVoteEvent.OnClickBackButton -> setDialogState(FirstVoteDialog.ExitDialog)
      is FirstVoteEvent.OnSearchKeywordChange -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
      is FirstVoteEvent.OnClickFoodItem -> updateState(FirstVoteReduce.UpdateFoodStatus(event.foodItem))
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
      is FirstVoteReduce.UpdateFoodItemList -> state.copy(foodItemList = reduce.foodItemList)
      is FirstVoteReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is FirstVoteReduce.UpdateFoodStatus -> state.copy(
        foodItemList = state.foodItemList.map { if (it == reduce.foodItem) it.nextStatus() else it }
      )
      is FirstVoteReduce.UpdateFinished -> state.copy(finished = reduce.finished)
      is FirstVoteReduce.UpdateCalculating -> state.copy(calculating = reduce.calculating)

      is FirstVoteReduce.UpdateTemplate -> state.copy(template = reduce.template)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(FirstVoteSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser
    val user = userRepository.getUserById(userId).asUI()
    updateState(FirstVoteReduce.UpdateUser(user))

    val loungeIdKey = LunchVoteNavRoute.FirstVote.arguments.first().name
    val loungeId = checkNotNull(savedStateHandle.get<String>(loungeIdKey))
    val lounge = loungeRepository.getLoungeById(loungeId).asUI()
    updateState(FirstVoteReduce.UpdateLounge(lounge))

    loungeStatusFlow = launch { collectLoungeStatus(lounge.id) }
    memberListFlow = launch { collectMemberList(lounge.id) }

    val foodItemList = foodRepository.getAllFood().map { food ->
      val imageUri = storageRepository.getFoodImageUri(food.name).toUri()
      FoodItem(
        food = food.asUI(),
        imageUri = imageUri,
        status = FoodItem.Status.DEFAULT
      )
    }
    updateState(FirstVoteReduce.UpdateFoodItemList(foodItemList))

    val templateList = templateRepository.getTemplateList(userId).map { it.asUI() }

    setDialogState(FirstVoteDialog.SelectTemplateDialog(templateList))
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatusFlowById(loungeId).collectLatest { status ->
      when(status) {
        Lounge.Status.QUIT -> {
          sendSideEffect(FirstVoteSideEffect.ShowSnackbar(UiText.StringResource(R.string.first_vote_owner_exited_snackbar)))
          sendSideEffect(FirstVoteSideEffect.PopBackStack)
        }
        Lounge.Status.SECOND_VOTE -> sendSideEffect(FirstVoteSideEffect.NavigateToSecondVote(currentState.lounge.id))
        else -> Unit
      }
    }
  }

  private suspend fun collectMemberList(loungeId: String) {
    memberRepository.getMemberListFlow(loungeId).collectLatest { memberList ->
      updateState(FirstVoteReduce.UpdateMemberList(memberList.map { it.asUI() }))

      if (memberList.size <= 1) {
        sendSideEffect(FirstVoteSideEffect.ShowSnackbar(UiText.StringResource(R.string.first_vote_only_owner_snackbar)))
        sendSideEffect(FirstVoteSideEffect.PopBackStack)
      }
      if (memberList.all { it.status == Member.Status.VOTED }) launch { submitVote() }
    }
  }

  private suspend fun selectTemplate(template: TemplateUIModel?) {
    if (template != null) {
      val foodItemList = foodRepository.getAllFood().map { food ->
        val imageUri = storageRepository.getFoodImageUri(food.name).toUri()
        FoodItem(
          food = food.asUI(),
          imageUri = imageUri,
          status = when(food.id) {
            in template.likedFoodIds -> FoodItem.Status.LIKE
            in template.dislikedFoodIds -> FoodItem.Status.DISLIKE
            else -> FoodItem.Status.DEFAULT
          }
        )
      }

      updateState(FirstVoteReduce.UpdateFoodItemList(foodItemList))

      setDialogState(null)
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

    val likedFoodsId = currentState.foodItemList.filter { it.status == FoodItem.Status.LIKE }.map { it.food.id }
    val dislikedFoodsId = currentState.foodItemList.filter { it.status == FoodItem.Status.DISLIKE }.map { it.food.id }
    val ballot = FirstBallotUIModel(
      loungeId = currentState.lounge.id,
      userId = me.userId,
      likedFoodIds = likedFoodsId,
      dislikedFoodIds = dislikedFoodsId
    )
    ballotRepository.submitFirstBallot(ballot.asDomain())

    if (me.userId == owner.userId) {
      calculateFirstVote(currentState.lounge.id)
      startSecondVote(currentState.lounge.id)
    }
  }

  private suspend fun exitVote() {
    loungeStatusFlow.cancel()
    memberListFlow.cancel()

    exitLounge(me.asDomain())

    sendSideEffect(FirstVoteSideEffect.PopBackStack)
  }
}