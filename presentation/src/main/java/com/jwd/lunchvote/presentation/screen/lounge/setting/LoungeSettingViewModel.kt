package com.jwd.lunchvote.presentation.screen.lounge.setting

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.usecase.UpdateLoungeSetting
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingReduce
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.LoungeSettingState
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MaxMembersDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MaxMembersDialogReduce
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MaxMembersDialogState
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinDislikeFoodsDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinDislikeFoodsDialogReduce
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinDislikeFoodsDialogState
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinLikeFoodsDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinLikeFoodsDialogReduce
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.MinLikeFoodsDialogState
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.SecondVoteCandidatesDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.SecondVoteCandidatesDialogReduce
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.SecondVoteCandidatesDialogState
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.TimeLimitDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.TimeLimitDialogReduce
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingContract.TimeLimitDialogState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kr.co.inbody.config.error.LoungeError
import kr.co.inbody.config.error.MemberError
import kr.co.inbody.config.error.RouteError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class LoungeSettingViewModel @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val memberRepository: MemberRepository,
  private val updateLoungeSetting: UpdateLoungeSetting,
  savedStateHandle: SavedStateHandle
) : BaseStateViewModel<LoungeSettingState, LoungeSettingEvent, LoungeSettingReduce, LoungeSettingSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoungeSettingState {
    return savedState as? LoungeSettingState ?: LoungeSettingState()
  }

  private val loungeId: String =
    savedStateHandle[LunchVoteNavRoute.LoungeSetting.arguments.first().name] ?: throw RouteError.NoArguments
  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: LoungeSettingEvent) {
    when (event) {
      is LoungeSettingEvent.ScreenInitialize -> launch { initialize() }

      is LoungeSettingEvent.OnClickBackButton -> sendSideEffect(LoungeSettingSideEffect.PopBackStack)
      is LoungeSettingEvent.OnClickTimeLimitItem -> updateState(LoungeSettingReduce.UpdateTimeLimitDialogState(TimeLimitDialogState(currentState.lounge.timeLimit)))
      is LoungeSettingEvent.OnClickMaxMembersItem -> updateState(LoungeSettingReduce.UpdateMaxMembersDialogState(MaxMembersDialogState(currentState.lounge.maxMembers)))
      is LoungeSettingEvent.OnClickSecondVoteCandidatesItem -> updateState(LoungeSettingReduce.UpdateSecondVoteCandidatesDialogState(SecondVoteCandidatesDialogState(currentState.lounge.secondVoteCandidates)))
      is LoungeSettingEvent.OnClickMinLikeFoodsItem -> updateState(LoungeSettingReduce.UpdateMinLikeFoodsDialogState(MinLikeFoodsDialogState(currentState.lounge.minLikeFoods)))
      is LoungeSettingEvent.OnClickMinDislikeFoodsItem -> updateState(LoungeSettingReduce.UpdateMinDislikeFoodsDialogState(MinDislikeFoodsDialogState(currentState.lounge.minDislikeFoods)))

      is TimeLimitDialogEvent -> handleTimeLimitDialogEvents(event)
      is MaxMembersDialogEvent -> handleMaxMembersDialogEvents(event)
      is SecondVoteCandidatesDialogEvent -> handleSecondVoteCandidatesDialogEvents(event)
      is MinLikeFoodsDialogEvent -> handleMinLikeFoodsDialogEvents(event)
      is MinDislikeFoodsDialogEvent -> handleMinDislikeFoodsDialogEvents(event)
    }
  }

  private fun handleTimeLimitDialogEvents(event: TimeLimitDialogEvent) {
    when (event) {
      is TimeLimitDialogEvent.OnClickDecreaseButton -> updateState(TimeLimitDialogReduce.DecreaseTimeLimit)
      is TimeLimitDialogEvent.OnClickIncreaseButton -> updateState(TimeLimitDialogReduce.IncreaseTimeLimit)
      is TimeLimitDialogEvent.OnClickCancelButton -> updateState(LoungeSettingReduce.UpdateTimeLimitDialogState(null))
      is TimeLimitDialogEvent.OnClickConfirmButton -> launch { changeTimeLimit() }
    }
  }

  private fun handleMaxMembersDialogEvents(event: MaxMembersDialogEvent) {
    when (event) {
      is MaxMembersDialogEvent.OnClickDecreaseButton -> updateState(MaxMembersDialogReduce.DecreaseMaxMembers)
      is MaxMembersDialogEvent.OnClickIncreaseButton -> updateState(MaxMembersDialogReduce.IncreaseMaxMembers)
      is MaxMembersDialogEvent.OnClickCancelButton -> updateState(LoungeSettingReduce.UpdateMaxMembersDialogState(null))
      is MaxMembersDialogEvent.OnClickConfirmButton -> launch { changeMaxMembers() }
    }
  }

  private fun handleSecondVoteCandidatesDialogEvents(event: SecondVoteCandidatesDialogEvent) {
    when (event) {
      is SecondVoteCandidatesDialogEvent.OnClickDecreaseButton -> updateState(SecondVoteCandidatesDialogReduce.DecreaseSecondVoteCandidates)
      is SecondVoteCandidatesDialogEvent.OnClickIncreaseButton -> updateState(SecondVoteCandidatesDialogReduce.IncreaseSecondVoteCandidates)
      is SecondVoteCandidatesDialogEvent.OnClickCancelButton -> updateState(LoungeSettingReduce.UpdateSecondVoteCandidatesDialogState(null))
      is SecondVoteCandidatesDialogEvent.OnClickConfirmButton -> launch { changeSecondVoteCandidates() }
    }
  }

  private fun handleMinLikeFoodsDialogEvents(event: MinLikeFoodsDialogEvent) {
    when (event) {
      is MinLikeFoodsDialogEvent.OnClickDecreaseButton -> updateState(MinLikeFoodsDialogReduce.DecreaseMinLikeFoods)
      is MinLikeFoodsDialogEvent.OnClickIncreaseButton -> updateState(MinLikeFoodsDialogReduce.IncreaseMinLikeFoods)
      is MinLikeFoodsDialogEvent.OnClickCancelButton -> updateState(LoungeSettingReduce.UpdateMinLikeFoodsDialogState(null))
      is MinLikeFoodsDialogEvent.OnClickConfirmButton -> launch { changeMinLikeFoods() }
    }
  }

  private fun handleMinDislikeFoodsDialogEvents(event: MinDislikeFoodsDialogEvent) {
    when (event) {
      is MinDislikeFoodsDialogEvent.OnClickDecreaseButton -> updateState(MinDislikeFoodsDialogReduce.DecreaseMinDislikeFoods)
      is MinDislikeFoodsDialogEvent.OnClickIncreaseButton -> updateState(MinDislikeFoodsDialogReduce.IncreaseMinDislikeFoods)
      is MinDislikeFoodsDialogEvent.OnClickCancelButton -> updateState(LoungeSettingReduce.UpdateMinDislikeFoodsDialogState(null))
      is MinDislikeFoodsDialogEvent.OnClickConfirmButton -> launch { changeMinDislikeFoods() }
    }
  }

  override fun reduceState(state: LoungeSettingState, reduce: LoungeSettingReduce): LoungeSettingState {
    return when (reduce) {
      is LoungeSettingReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is LoungeSettingReduce.UpdateIsOwner -> state.copy(isOwner = reduce.isOwner)
      is LoungeSettingReduce.UpdateTimeLimitDialogState -> state.copy(timeLimitDialogState = reduce.timeLimitDialogState)
      is LoungeSettingReduce.UpdateMaxMembersDialogState -> state.copy(maxMembersDialogState = reduce.maxMembersDialogState)
      is LoungeSettingReduce.UpdateSecondVoteCandidatesDialogState -> state.copy(secondVoteCandidatesDialogState = reduce.secondVoteCandidatesDialogState)
      is LoungeSettingReduce.UpdateMinLikeFoodsDialogState -> state.copy(minLikeFoodsDialogState = reduce.minLikeFoodsDialogState)
      is LoungeSettingReduce.UpdateMinDislikeFoodsDialogState -> state.copy(minDislikeFoodsDialogState = reduce.minDislikeFoodsDialogState)

      is TimeLimitDialogReduce -> state.copy(timeLimitDialogState = reduceTimeLimitDialogState(state.timeLimitDialogState, reduce))
      is MaxMembersDialogReduce -> state.copy(maxMembersDialogState = reduceMaxMembersDialogState(state.maxMembersDialogState, reduce))
      is SecondVoteCandidatesDialogReduce -> state.copy(secondVoteCandidatesDialogState = reduceSecondVoteCandidatesDialogState(state.secondVoteCandidatesDialogState, reduce))
      is MinLikeFoodsDialogReduce -> state.copy(minLikeFoodsDialogState = reduceMinLikeFoodsDialogState(state.minLikeFoodsDialogState, reduce))
      is MinDislikeFoodsDialogReduce -> state.copy(minDislikeFoodsDialogState = reduceMinDislikeFoodsDialogState(state.minDislikeFoodsDialogState, reduce))
    }
  }

  private fun reduceTimeLimitDialogState(state: TimeLimitDialogState?, reduce: TimeLimitDialogReduce): TimeLimitDialogState? {
    return when (reduce) {
      is TimeLimitDialogReduce.DecreaseTimeLimit -> state?.copy(
        timeLimit = when (state.timeLimit) {
          20 -> 10
          30 -> 20
          60 -> 30
          90 -> 60
          120 -> 90
          null -> 120
          else -> null
        }
      )
      is TimeLimitDialogReduce.IncreaseTimeLimit -> state?.copy(
        timeLimit = when (state.timeLimit) {
          10 -> 20
          20 -> 30
          30 -> 60
          60 -> 90
          90 -> 120
          120 -> null
          else -> 10
        }
      )
    }
  }

  private fun reduceMaxMembersDialogState(state: MaxMembersDialogState?, reduce: MaxMembersDialogReduce): MaxMembersDialogState? {
    return when (reduce) {
      is MaxMembersDialogReduce.DecreaseMaxMembers -> state?.copy(maxMembers = state.maxMembers - 1)
      is MaxMembersDialogReduce.IncreaseMaxMembers -> state?.copy(maxMembers = state.maxMembers + 1)
    }
  }

  private fun reduceSecondVoteCandidatesDialogState(state: SecondVoteCandidatesDialogState?, reduce: SecondVoteCandidatesDialogReduce): SecondVoteCandidatesDialogState? {
    return when (reduce) {
      is SecondVoteCandidatesDialogReduce.DecreaseSecondVoteCandidates -> state?.copy(secondVoteCandidates = state.secondVoteCandidates - 1)
      is SecondVoteCandidatesDialogReduce.IncreaseSecondVoteCandidates -> state?.copy(secondVoteCandidates = state.secondVoteCandidates + 1)
    }
  }

  private fun reduceMinLikeFoodsDialogState(state: MinLikeFoodsDialogState?, reduce: MinLikeFoodsDialogReduce): MinLikeFoodsDialogState? {
    return when (reduce) {
      is MinLikeFoodsDialogReduce.DecreaseMinLikeFoods -> state?.copy(minLikeFoods = state.minLikeFoods?.minus(1))
      is MinLikeFoodsDialogReduce.IncreaseMinLikeFoods -> state?.copy(minLikeFoods = state.minLikeFoods?.plus(1))
    }
  }

  private fun reduceMinDislikeFoodsDialogState(state: MinDislikeFoodsDialogState?, reduce: MinDislikeFoodsDialogReduce): MinDislikeFoodsDialogState? {
    return when (reduce) {
      is MinDislikeFoodsDialogReduce.DecreaseMinDislikeFoods -> state?.copy(minDislikeFoods = state.minDislikeFoods?.minus(1))
      is MinDislikeFoodsDialogReduce.IncreaseMinDislikeFoods -> state?.copy(minDislikeFoods = state.minDislikeFoods?.plus(1))
    }
  }

  override fun handleErrors(error: Throwable) {
    when (error) {
      is LoungeError.LoungeSettingFailed -> {
        updateState(LoungeSettingReduce.UpdateTimeLimitDialogState(null))
        updateState(LoungeSettingReduce.UpdateMaxMembersDialogState(null))
        updateState(LoungeSettingReduce.UpdateSecondVoteCandidatesDialogState(null))
        updateState(LoungeSettingReduce.UpdateMinLikeFoodsDialogState(null))
        updateState(LoungeSettingReduce.UpdateMinDislikeFoodsDialogState(null))
      }
      else -> Unit
    }
    sendSideEffect(LoungeSettingSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    launch { collectLounge(loungeId) }

    val me = memberRepository.getMemberListFlow(loungeId).first().find { it.userId == userId } ?: throw MemberError.InvalidMember
    val isOwner = me.type == Member.Type.OWNER

    updateState(LoungeSettingReduce.UpdateIsOwner(isOwner))
  }

  private suspend fun collectLounge(loungeId: String) {
    loungeRepository.getLoungeFlowById(loungeId).collectLatest { lounge ->
      updateState(LoungeSettingReduce.UpdateLounge(lounge.asUI()))
    }
  }

  private suspend fun changeTimeLimit() {
    val dialogState = currentState.timeLimitDialogState ?: return
    if (currentState.lounge.timeLimit == dialogState.timeLimit) return
    updateState(LoungeSettingReduce.UpdateTimeLimitDialogState(null))

    updateLoungeSetting(
      loungeId = currentState.lounge.id,
      timeLimit = dialogState.timeLimit
    )

    sendEvent(LoungeSettingEvent.ScreenInitialize)
  }

  private suspend fun changeMaxMembers() {
    val dialogState = currentState.maxMembersDialogState ?: return
    if (currentState.lounge.maxMembers == dialogState.maxMembers) return
    updateState(LoungeSettingReduce.UpdateMaxMembersDialogState(null))

    updateLoungeSetting(
      loungeId = currentState.lounge.id,
      maxMembers = dialogState.maxMembers
    )

    sendEvent(LoungeSettingEvent.ScreenInitialize)
  }

  private suspend fun changeSecondVoteCandidates() {
    val dialogState = currentState.secondVoteCandidatesDialogState ?: return
    if (currentState.lounge.secondVoteCandidates == dialogState.secondVoteCandidates) return
    updateState(LoungeSettingReduce.UpdateSecondVoteCandidatesDialogState(null))

    updateLoungeSetting(
      loungeId = currentState.lounge.id,
      secondVoteCandidates = dialogState.secondVoteCandidates
    )

    sendEvent(LoungeSettingEvent.ScreenInitialize)
  }

  private suspend fun changeMinLikeFoods() {
    val dialogState = currentState.minLikeFoodsDialogState ?: return
    if (currentState.lounge.minLikeFoods == dialogState.minLikeFoods) return
    updateState(LoungeSettingReduce.UpdateMinLikeFoodsDialogState(null))

    updateLoungeSetting(
      loungeId = currentState.lounge.id,
      minLikeFoods = dialogState.minLikeFoods
    )

    sendEvent(LoungeSettingEvent.ScreenInitialize)
  }

  private suspend fun changeMinDislikeFoods() {
    val dialogState = currentState.minDislikeFoodsDialogState ?: return
    if (currentState.lounge.minDislikeFoods == dialogState.minDislikeFoods) return
    updateState(LoungeSettingReduce.UpdateMinDislikeFoodsDialogState(null))

    updateLoungeSetting(
      loungeId = currentState.lounge.id,
      minDislikeFoods = dialogState.minDislikeFoods
    )

    sendEvent(LoungeSettingEvent.ScreenInitialize)
  }
}