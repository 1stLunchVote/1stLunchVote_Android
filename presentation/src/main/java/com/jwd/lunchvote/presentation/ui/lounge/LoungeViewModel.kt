package com.jwd.lunchvote.presentation.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.type.LoungeStatusType
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.usecase.CheckMemberStatusUseCase
import com.jwd.lunchvote.domain.usecase.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.ExitLoungeUseCase
import com.jwd.lunchvote.domain.usecase.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.GetLoungeByIdUseCase
import com.jwd.lunchvote.domain.usecase.GetLoungeStatusUseCase
import com.jwd.lunchvote.domain.usecase.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.JoinLoungeUseCase
import com.jwd.lunchvote.domain.usecase.SendChatUseCase
import com.jwd.lunchvote.domain.usecase.UpdateReadyUseCase
import com.jwd.lunchvote.presentation.mapper.ChatUIMapper
import com.jwd.lunchvote.presentation.mapper.MemberUIMapper
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
import com.jwd.lunchvote.presentation.model.type.MessageUIType
import com.jwd.lunchvote.presentation.model.type.SendStatusUIType
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeEvent
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeReduce
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.LoungeContract.LoungeState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
  private val joinLoungeUseCase: JoinLoungeUseCase,
  private val createLoungeUseCase: CreateLoungeUseCase,
  private val getLoungeByIdUseCase: GetLoungeByIdUseCase,
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

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun openDialog(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  // todo : 이미 시작된 방인 경우 뒤로가기 처리 필요

  private var currentJob: Job? = null

  init {
    val loungeId = savedStateHandle.get<String?>(LunchVoteNavRoute.Lounge.arguments.first().name)
    if (loungeId != null) {
      joinLounge(loungeId)
      getLoungeData(loungeId)
    } else {
      launch { createLounge() }
    }
  }

  override fun handleEvents(event: LoungeEvent) {
    when (event) {
      is LoungeEvent.OnClickBackButton -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeEvent.OnChatChanged -> updateState(LoungeReduce.UpdateChat(event.chat))
      is LoungeEvent.OnClickSendChatButton -> launch { sendChat() }

      is LoungeEvent.OnClickReadyButton -> {
        val owner = currentState.memberList.first { it.status == MemberStatusUIType.OWNER }
        if (currentState.user.id == owner.id) {
          if (currentState.memberList.any { it.status != MemberStatusUIType.READY }) {
            sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("모든 멤버가 준비해야합니다.")))
          } else {
            // TODO: Start Vote
          }
        } else updateReady()
      }

      is LoungeEvent.OnClickInviteButton -> sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.loungeId ?: throw LoungeError.NoLounge))
      is LoungeEvent.OnScrolled -> updateState(LoungeReduce.UpdateScrollIndex(event.index))

      // DialogEvents
      is LoungeEvent.OnClickCancelButtonVoteExitDialog -> sendSideEffect(LoungeSideEffect.CloseDialog)
      is LoungeEvent.OnClickConfirmButtonVoteExitDialog -> launch { exitLounge() }
    }
  }

  override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
    return when (reduce) {
      is LoungeReduce.UpdateUser -> state.copy(user = reduce.user)
      is LoungeReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is LoungeReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
      is LoungeReduce.UpdateChatList -> state.copy(chatList = reduce.chatList)
      is LoungeReduce.UpdateChat -> state.copy(chat = reduce.chat)
      is LoungeReduce.UpdateScrollIndex -> state.copy(scrollIndex = reduce.index)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun createLounge() {
    withTimeoutOrNull(TIMEOUT) {
      val loungeId = createLoungeUseCase(currentState.user.asDomain())
      val lounge = getLoungeByIdUseCase(loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      getLoungeData(res)
      checkMemberStatus()
    } ?: run {
      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("방 생성에 실패하였습니다.")))
      sendSideEffect(LoungeSideEffect.PopBackStack)
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

  private suspend fun sendChat() {
    updateState(LoungeReduce.UpdateChat(""))

    val chat = LoungeChatUIModel(
      id = UUID.randomUUID().toString(),
      userId = currentState.user.id,
      userName = currentState.user.name,
      userProfile = currentState.user.profileImageUrl,
      message = currentState.chat,
      messageType = MessageUIType.NORMAL,
      sendStatus = SendStatusUIType.SENDING,
      createdAt = ZonedDateTime.now().toString()
    )
    sendChatUseCase.invoke(chat.asDomain())
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
    currentJob = launch {
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
                currentJob?.cancel()

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

  private suspend fun exitLounge() {
    currentJob?.cancel()

    val loungeId = currentState.loungeId ?: throw LoungeError.NoLounge
    val userId = auth.currentUser?.uid ?: throw LoginError.NoUser

    exitLoungeUseCase(userId, loungeId)

    sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("투표 대기방에서 나왔습니다.")))
    sendSideEffect(LoungeSideEffect.CloseDialog)
    sendSideEffect(LoungeSideEffect.PopBackStack)
  }

  companion object {
    private const val TIMEOUT = 10000L
    private const val LOUNGE_KEY_ID = "id"
  }
}