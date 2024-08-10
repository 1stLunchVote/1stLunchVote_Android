package com.jwd.lunchvote.presentation.ui.lounge.setting

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.usecase.UpdateLoungeSetting
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.Companion.MAX_MEMBERS_DIALOG
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.Companion.MIN_DISLIKE_FOODS_DIALOG
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.Companion.MIN_LIKE_FOODS_DIALOG
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.Companion.SECOND_VOTE_CANDIDATES_DIALOG
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.Companion.TIME_LIMIT_DIALOG
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingEvent
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingReduce
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingContract.LoungeSettingState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.RouteError
import javax.inject.Inject

@HiltViewModel
class LoungeSettingViewModel @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val updateLoungeSetting: UpdateLoungeSetting,
  savedStateHandle: SavedStateHandle
) : BaseStateViewModel<LoungeSettingState, LoungeSettingEvent, LoungeSettingReduce, LoungeSettingSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoungeSettingState {
    return savedState as? LoungeSettingState ?: LoungeSettingState()
  }

  private val loungeId: String =
    savedStateHandle[LunchVoteNavRoute.LoungeSetting.arguments.first().name] ?: throw RouteError.NoArguments

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun openDialog(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: LoungeSettingEvent) {
    when (event) {
      is LoungeSettingEvent.ScreenInitialize -> launch { initialize() }

      is LoungeSettingEvent.OnClickBackButton -> sendSideEffect(LoungeSettingSideEffect.PopBackStack)
      is LoungeSettingEvent.OnClickTimeLimitItem -> sendSideEffect(LoungeSettingSideEffect.OpenTimeLimitDialog)
      is LoungeSettingEvent.OnClickMaxMembersItem -> sendSideEffect(LoungeSettingSideEffect.OpenMaxMembersDialog)
      is LoungeSettingEvent.OnClickSecondVoteCandidatesItem -> sendSideEffect(LoungeSettingSideEffect.OpenSecondVoteCandidatesDialog)
      is LoungeSettingEvent.OnClickMinLikeFoodsItem -> sendSideEffect(LoungeSettingSideEffect.OpenMinLikeFoodsDialog)
      is LoungeSettingEvent.OnClickMinDislikeFoodsItem -> sendSideEffect(LoungeSettingSideEffect.OpenMinDislikeFoodsDialog)

      // DialogEvents
      is LoungeSettingEvent.OnClickCancelButtonDialog -> sendSideEffect(LoungeSettingSideEffect.CloseDialog)
      is LoungeSettingEvent.OnClickConfirmButtonDialog -> launch {
        val dialog = _dialogState.value
        sendSideEffect(LoungeSettingSideEffect.CloseDialog)

        changeSetting(dialog, event.value)
      }
    }
  }

  override fun reduceState(state: LoungeSettingState, reduce: LoungeSettingReduce): LoungeSettingState {
    return when (reduce) {
      is LoungeSettingReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSettingSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val lounge = loungeRepository.getLoungeById(loungeId).asUI()

    updateState(LoungeSettingReduce.UpdateLounge(lounge))
  }

  private suspend fun changeSetting(dialog: String, value: Int?) {
    when (dialog) {
      TIME_LIMIT_DIALOG -> {
        if (currentState.lounge.timeLimit == value) return
        updateLoungeSetting(
          loungeId = currentState.lounge.id,
          timeLimit = value
        )
      }
      MAX_MEMBERS_DIALOG -> {
        if (currentState.lounge.maxMembers == value) return
        updateLoungeSetting(
          loungeId = currentState.lounge.id,
          maxMembers = value
        )
      }
      SECOND_VOTE_CANDIDATES_DIALOG -> {
        if (currentState.lounge.secondVoteCandidates == value) return
        updateLoungeSetting(
          loungeId = currentState.lounge.id,
          secondVoteCandidates = value
        )
      }
      MIN_LIKE_FOODS_DIALOG -> {
        if (currentState.lounge.minLikeFoods == value) return
        updateLoungeSetting(
          loungeId = currentState.lounge.id,
          minLikeFoods = value
        )
      }
      MIN_DISLIKE_FOODS_DIALOG -> {
        if (currentState.lounge.minDislikeFoods == value) return
        updateLoungeSetting(
          loungeId = currentState.lounge.id,
          minDislikeFoods = value
        )
      }
      else -> return
    }

    sendEvent(LoungeSettingEvent.ScreenInitialize)
  }
}