package com.jwd.lunchvote.presentation.screen.lounge.member

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.usecase.ExileMember
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberEvent
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberReduce
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberContract.LoungeMemberState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.MemberError
import kr.co.inbody.config.error.RouteError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class LoungeMemberViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val memberRepository: MemberRepository,
  private val exileMember: ExileMember,
  savedStateHandle: SavedStateHandle,
) : BaseStateViewModel<LoungeMemberState, LoungeMemberEvent, LoungeMemberReduce, LoungeMemberSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoungeMemberState {
    return savedState as? LoungeMemberState ?: LoungeMemberState()
  }

  private val memberUserId: String =
    savedStateHandle[LunchVoteNavRoute.LoungeMember.arguments[0].name] ?: throw RouteError.NoArguments
  private val loungeId: String =
    savedStateHandle[LunchVoteNavRoute.LoungeMember.arguments[1].name] ?: throw RouteError.NoArguments

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

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
      is LoungeMemberReduce.UpdateMe -> state.copy(me = reduce.me)
      is LoungeMemberReduce.UpdateMember -> state.copy(member = reduce.member)
      is LoungeMemberReduce.UpdateUser -> state.copy(user = reduce.user)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeMemberSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    when (error) {
      is MemberError.InvalidMember -> sendSideEffect(LoungeMemberSideEffect.PopBackStack)
    }
  }

  private suspend fun initialize() {
    val me = memberRepository.getMember(userId, loungeId)?.asUI() ?: throw MemberError.InvalidMember
    val member = memberRepository.getMember(memberUserId, loungeId)?.asUI() ?: throw MemberError.InvalidMember
    val user = userRepository.getUserById(member.userId).asUI()

    updateState(LoungeMemberReduce.UpdateMe(me))
    updateState(LoungeMemberReduce.UpdateMember(member))
    updateState(LoungeMemberReduce.UpdateUser(user))
  }

  private suspend fun exileMember() {
    sendSideEffect(LoungeMemberSideEffect.CloseDialog)

    exileMember(currentState.member.asDomain())

    sendSideEffect(LoungeMemberSideEffect.PopBackStack)
  }
}