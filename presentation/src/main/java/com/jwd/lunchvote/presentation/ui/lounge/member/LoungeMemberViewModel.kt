package com.jwd.lunchvote.presentation.ui.lounge.member

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.usecase.lounge.CheckMemberStatusUseCase
import com.jwd.lunchvote.domain.usecase.lounge.ExileMemberUseCase
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberEvent
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberReduce
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoungeMemberViewModel @Inject constructor(
  private val exileMemberUseCase: ExileMemberUseCase,
  auth: FirebaseAuth,
  checkMemberStatusUseCase: CheckMemberStatusUseCase,
  savedStateHandle: SavedStateHandle,
) :
  BaseStateViewModel<LoungeMemberState, LoungeMemberEvent, LoungeMemberReduce, LoungeMemberSideEffect>(
    savedStateHandle
  ) {
  private val memberId = checkNotNull(savedStateHandle.get<String?>(MEMBER_EXTRA_KEY))
  private val loungeId = checkNotNull(savedStateHandle.get<String?>(LOUNGE_EXTRA_KEY))
  private val nickname = checkNotNull(savedStateHandle.get<String?>(NICKNAME_EXTRA_KEY))
  private val profileUrl = savedStateHandle.get<String?>(PROFILE_URL_EXTRA_KEY)
  private val isOwner = savedStateHandle[IS_OWNER_EXTRA_KEY] ?: false

  override fun createInitialState(savedState: Parcelable?): LoungeMemberState {
    return savedState as? LoungeMemberState ?: LoungeMemberState()
  }

  init {
    updateState(LoungeMemberReduce.SetMemberInfo(memberId, nickname, profileUrl, isOwner))

    checkMemberStatusUseCase(auth.currentUser?.uid!!, loungeId)
      .onEach {
        when (it) {
          MemberStatusType.EXILED, MemberStatusType.EXITED -> {
            sendSideEffect(LoungeMemberSideEffect.PopBackStack)
          }

          else -> {}
        }
      }
      .launchIn(viewModelScope)
  }

  override fun reduceState(
    state: LoungeMemberState,
    reduce: LoungeMemberReduce,
  ): LoungeMemberState {
    return when (reduce) {
      is LoungeMemberReduce.SetMemberInfo -> {
        state.copy(
          memberId = reduce.memberId,
          nickname = reduce.nickname,
          profileUrl = reduce.profileUrl,
          isOwner = reduce.isOwner
        )
      }
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeMemberSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private fun exileMember() {
    launch {
      exileMemberUseCase(memberId, loungeId)

      sendSideEffect(LoungeMemberSideEffect.PopBackStack)
    }
  }

  override fun handleEvents(event: LoungeMemberEvent) {
    when (event) {
      is LoungeMemberEvent.OnClickExile -> exileMember()
    }
  }

  companion object {
    private const val MEMBER_EXTRA_KEY = "id"
    private const val LOUNGE_EXTRA_KEY = "loungeId"
    private const val NICKNAME_EXTRA_KEY = "nickname"
    private const val PROFILE_URL_EXTRA_KEY = "profileUrl"
    private const val IS_OWNER_EXTRA_KEY = "isOwner"
  }
}