package com.jwd.lunchvote.presentation.ui.lounge.member

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.common.error.UserError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.ExileMemberUseCase
import com.jwd.lunchvote.domain.usecase.GetMemberByUserIdUseCase
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberEvent
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberReduce
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoungeMemberViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val getMemberByUserIdUseCase: GetMemberByUserIdUseCase,
  private val exileMemberUseCase: ExileMemberUseCase,
  private val savedStateHandle: SavedStateHandle,
) : BaseStateViewModel<LoungeMemberState, LoungeMemberEvent, LoungeMemberReduce, LoungeMemberSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoungeMemberState {
    return savedState as? LoungeMemberState ?: LoungeMemberState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun openDialog(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: LoungeMemberEvent) {
    when (event) {
      is LoungeMemberEvent.ScreenInitialize -> launch { initialize() }
      is LoungeMemberEvent.OnClickBackButton -> sendSideEffect(LoungeMemberSideEffect.PopBackStack)
      is LoungeMemberEvent.OnClickExileButton -> sendSideEffect(LoungeMemberSideEffect.OpenExileConfirmDialog)

      // DialogEvents
      is LoungeMemberEvent.OnClickCancelButtonExileConfirmDialog -> sendSideEffect(LoungeMemberSideEffect.CloseDialog)
      is LoungeMemberEvent.OnClickConfirmButtonExileConfirmDialog -> launch { exileMember() }
    }
  }

  override fun reduceState(state: LoungeMemberState, reduce: LoungeMemberReduce, ): LoungeMemberState {
    return when (reduce) {
      is LoungeMemberReduce.UpdateMember -> state.copy(member = reduce.member)
      is LoungeMemberReduce.UpdateUser -> state.copy(user = reduce.user)
      is LoungeMemberReduce.UpdateIsMe -> state.copy(isMe = reduce.isMe)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeMemberSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
    when (error) {
      is LoungeError.InvalidMember -> sendSideEffect(LoungeMemberSideEffect.PopBackStack)
    }
  }

  private suspend fun initialize() {
    val userIdKey = LunchVoteNavRoute.LoungeMember.arguments.first().name
    val userId = checkNotNull(savedStateHandle.get<String>(userIdKey))
    val loungeIdKey = LunchVoteNavRoute.LoungeMember.arguments.last().name
    val loungeId = checkNotNull(savedStateHandle.get<String>(loungeIdKey))

    val member = getMemberByUserIdUseCase(userId, loungeId).asUI()
    val user = userRepository.getUserById(member.userId).asUI()
    val authUser = Firebase.auth.currentUser ?: throw UserError.NoUser
    val isMe = authUser.uid == member.userId

    updateState(LoungeMemberReduce.UpdateMember(member))
    updateState(LoungeMemberReduce.UpdateUser(user))
    updateState(LoungeMemberReduce.UpdateIsMe(isMe))
  }

  private suspend fun exileMember() {
    sendSideEffect(LoungeMemberSideEffect.CloseDialog)

    exileMemberUseCase(currentState.member.asDomain())

    sendSideEffect(LoungeMemberSideEffect.PopBackStack)
  }
}