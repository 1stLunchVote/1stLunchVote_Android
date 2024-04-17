package com.jwd.lunchvote.presentation.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.usecase.lounge.CheckMemberStatusUseCase
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.ExitLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetLoungeStatusUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.JoinLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.SendChatUseCase
import com.jwd.lunchvote.domain.usecase.lounge.UpdateReadyUseCase
import com.jwd.lunchvote.presentation.mapper.lounge.ChatUIMapper
import com.jwd.lunchvote.presentation.mapper.lounge.MemberUIMapper
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeEvent
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeReduce
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
  private val joinLoungeUseCase: JoinLoungeUseCase,
  private val createLoungeUseCase: CreateLoungeUseCase,
  private val getMemberListUseCase: GetMemberListUseCase,
  private val getChatListUseCase: GetChatListUseCase,
  private val getLoungeStatusUseCase: GetLoungeStatusUseCase,
  private val updateReadyUseCase: UpdateReadyUseCase,
  private val sendChatUseCase: SendChatUseCase,
  private val exitLoungeUseCase: ExitLoungeUseCase,
  private val checkMemberStatusUseCase: CheckMemberStatusUseCase,
  private val auth: FirebaseAuth,
  savedStateHandle: SavedStateHandle
) : BaseStateViewModel<LoungeState, LoungeEvent, LoungeReduce, LoungeSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): LoungeState {
    return savedState as? LoungeState ?: LoungeState()
  }
  // todo : 이미 시작된 방인 경우 뒤로가기 처리 필요

  private var checkJob: Job? = null

  init {
    savedStateHandle.get<String?>(LOUNGE_KEY_ID)?.let {
      joinLounge(it)
      getLoungeData(it)
    } ?: {
      createLounge()
      updateState(LoungeReduce.UpdateIsOwner(true))
    }
  }

  override fun handleEvents(event: LoungeEvent) {
    when (event) {
      is LoungeEvent.OnEditChat -> updateState(LoungeReduce.UpdateCurrentChat(event.chat))
      is LoungeEvent.OnSendChat -> {
        updateState(LoungeReduce.UpdateCurrentChat(""))
        sendChat()
      }

      is LoungeEvent.OnReady -> {
        if (currentState.isOwner && currentState.allReady.not()) {
          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("멤버 전원이 준비해야합니다.")))
        } else updateReady()
      }

      is LoungeEvent.OnTryExit -> {
        updateState(LoungeReduce.UpdateExitDialogShown(true))
      }

      is LoungeEvent.OnClickExit -> {
        if (event.exit) {
          exitLounge()
        }

        updateState(LoungeReduce.UpdateExitDialogShown(false))
      }

      is LoungeEvent.OnClickInvite -> sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.loungeId ?: throw LoungeError.NoLounge))
      is LoungeEvent.OnScrolled -> updateState(LoungeReduce.UpdateScrollIndex(event.index))
    }
  }

  override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
    return when (reduce) {
      is LoungeReduce.UpdateIsOwner -> state.copy(isOwner = reduce.isOwner)
      is LoungeReduce.UpdateLoungeId -> state.copy(loungeId = reduce.loungeId)
      is LoungeReduce.UpdateMemberList -> {
        state.copy(
          memberList = reduce.memberList,
          isReady = reduce.memberList.find { it.isMine }?.isReady ?: false,
          allReady = with(state) {
            memberList.filter { !it.isOwner }.all { it.isReady }
              || (!isOwner && isReady) || (memberList.size == 1 && isOwner)
          }
        )
      }

      is LoungeReduce.UpdateChatList -> state.copy(chatList = reduce.chatList.reversed())
      is LoungeReduce.UpdateCurrentChat -> state.copy(currentChat = reduce.chat)
      is LoungeReduce.UpdateExitDialogShown -> state.copy(exitDialogShown = reduce.shown)
      is LoungeReduce.UpdateScrollIndex -> state.copy(scrollIndex = reduce.index)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private fun createLounge() {
    launch {
      withTimeoutOrNull(TIMEOUT) {
        val res = createLoungeUseCase()

        updateState(LoungeReduce.UpdateLoungeId(res))

        getLoungeData(res)
        checkMemberStatus()
      } ?: run {
        sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("방 생성에 실패하였습니다.")))
        sendSideEffect(LoungeSideEffect.PopBackStack)
      }
    }
  }

  private fun joinLounge(loungeId: String) {
    launch {
      withTimeoutOrNull(TIMEOUT) {
        joinLoungeUseCase(loungeId)

        updateState(LoungeReduce.UpdateLoungeId(loungeId))

        checkMemberStatus()
      } ?: run {
        sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("입장에 실패하였습니다.")))
        sendSideEffect(LoungeSideEffect.PopBackStack)
      }
    }
  }

  private fun sendChat() {
    launch {
      sendChatUseCase(currentState.loungeId ?: throw LoungeError.NoLounge, currentState.currentChat)
    }
  }

  private fun getLoungeData(loungeId: String) {
    launch {
      getChatListUseCase(loungeId)
        .collectLatest {
          updateState(
            LoungeReduce.UpdateChatList(it.map { chat ->
              ChatUIMapper.mapToRight(chat, chat.userId == auth.currentUser?.uid)
            })
          )
        }
    }


    launch {
      getMemberListUseCase(loungeId)
        .collectLatest {
          updateState(LoungeReduce.UpdateMemberList(it.map { m ->
            MemberUIMapper.mapToRight(m, m.id == auth.currentUser?.uid)
          }))
        }
    }


    launch {
      getLoungeStatusUseCase(loungeId)
        .collectLatest {
          when (it) {
            LoungeStatusType.STARTED -> sendSideEffect(LoungeSideEffect.NavigateToVote(loungeId))
            else -> Unit
          }
        }
    }
  }

  private fun checkMemberStatus() {
    checkJob = launch {
      checkMemberStatusUseCase(
        auth.currentUser?.uid ?: throw LoginError.NoUser,
        currentState.loungeId ?: throw LoungeError.NoLounge
      )
        .collectLatest {
          when (it) {
            MemberStatusType.EXITED -> {
              sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("방장이 방을 종료하였습니다.")))
              sendSideEffect(LoungeSideEffect.PopBackStack)
            }

            MemberStatusType.EXILED -> {
              sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("방장에 의해 추방되었습니다.")))
              sendSideEffect(LoungeSideEffect.PopBackStack)

              CoroutineScope(Dispatchers.IO).launch {
                checkJob?.cancel()

                currentState.loungeId?.let { loungeId ->
                  exitLoungeUseCase(auth.currentUser?.uid ?: throw LoginError.NoUser, loungeId)
                }
              }
            }

            else -> {}
          }
        }
    }
  }

  private fun updateReady() {
    launch {
      runCatching {
        updateReadyUseCase(
          auth.currentUser?.uid ?: throw LoginError.NoUser,
          currentState.loungeId ?: throw LoungeError.NoLounge,
          currentState.isOwner
        )
      }.onFailure {
        sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString(if (currentState.isOwner) "게임 시작 실패" else "준비 상태 변경 실패")))
      }
    }
  }

  private fun exitLounge() {
    // 대기방에서 먼저 나오고 백그라운드로 라운 나오기
    CoroutineScope(Dispatchers.IO).launch {
      checkJob?.cancel()

      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("투표 대기방에서 나왔습니다.")))
      sendSideEffect(LoungeSideEffect.PopBackStack)

      currentState.loungeId?.let { loungeId ->
        exitLoungeUseCase(auth.currentUser?.uid ?: throw LoginError.NoUser, loungeId)
      } ?: throw LoungeError.NoLounge
    }
  }

  companion object {
    private const val TIMEOUT = 10000L
    private const val LOUNGE_KEY_ID = "id"
  }
}