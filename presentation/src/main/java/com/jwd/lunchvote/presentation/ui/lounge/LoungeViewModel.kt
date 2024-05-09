package com.jwd.lunchvote.presentation.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import com.jwd.lunchvote.domain.usecase.GetUserByIdUseCase
import com.jwd.lunchvote.domain.usecase.JoinLoungeUseCase
import com.jwd.lunchvote.domain.usecase.SendChatUseCase
import com.jwd.lunchvote.domain.usecase.UpdateReadyUseCase
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.type.LoungeStatusUIType
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
  private val getUserByIdUseCase: GetUserByIdUseCase,
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

  private var currentJob: Job? = null
  private val owner: MemberUIModel
    get() = currentState.memberList.find { it.status == MemberStatusUIType.OWNER } ?: throw LoungeError.InvalidLounge("방장 정보가 없습니다.")

  init {
    launch {
      val userId = Firebase.auth.currentUser?.uid ?: throw LoginError.NoUser
      val user = getUserByIdUseCase(userId).asUI()
      updateState(LoungeReduce.UpdateUser(user))

      val loungeId = savedStateHandle.get<String?>(LunchVoteNavRoute.Lounge.arguments.first().name)
      loungeId?.let {
        val lounge = getLoungeByIdUseCase(it).asUI()
        if (lounge.status != LoungeStatusUIType.CREATED) throw LoungeError.NoLounge

        joinLounge(it)
      } ?: createLounge()
    }
  }

  override fun handleEvents(event: LoungeEvent) {
    when (event) {
      is LoungeEvent.OnClickBackButton -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeEvent.OnClickMember -> sendSideEffect(
        LoungeSideEffect.NavigateToMember(
          member = event.member,
          loungeId = currentState.lounge.id,
          isOwner = currentState.user.id == owner.userId
        )
      )
      is LoungeEvent.OnClickInviteButton -> sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.lounge.id))
      is LoungeEvent.OnChatChanged -> updateState(LoungeReduce.UpdateChat(event.chat))
      is LoungeEvent.OnClickSendChatButton -> launch { sendChat() }

      is LoungeEvent.OnClickReadyButton -> launch {
        val owner = currentState.memberList.find { it.status == MemberStatusUIType.OWNER }
          ?: throw LoungeError.InvalidLounge("방장 정보가 없습니다.")
        if (currentState.user.id == owner.userId && currentState.memberList.any { it.status != MemberStatusUIType.READY }) {
          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("모든 멤버가 준비해야합니다.")))
        } else {
          updateReady()
        }
      }

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
    sendSideEffect(
      LoungeSideEffect.ShowSnackBar(
        UiText.DynamicString(
          error.message ?: UnknownError.UNKNOWN
        )
      )
    )
    when (error) {
      is LoungeError.NoLounge -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.InvalidMember -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.InvalidLounge -> sendSideEffect(LoungeSideEffect.PopBackStack)
      else -> {}
    }
  }

  private suspend fun createLounge() {
    withTimeoutOrNull(TIMEOUT) {
      val user = currentState.user
      val loungeId = createLoungeUseCase(user.asDomain())
      val lounge = getLoungeByIdUseCase(loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge)
    } ?: {
      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("방 생성에 실패하였습니다.")))
      sendSideEffect(LoungeSideEffect.PopBackStack)
    }
  }

  private suspend fun joinLounge(loungeId: String) {
    withTimeoutOrNull(TIMEOUT) {
      val user = currentState.user
      val lounge = joinLoungeUseCase(user.asDomain(), loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge)
    } ?: {
      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("입장에 실패하였습니다.")))
      sendSideEffect(LoungeSideEffect.PopBackStack)
    }
  }

  private suspend fun collectLoungeData(lounge: LoungeUIModel) {
    collectLoungeStatus(lounge.id)
    collectMemberList(lounge.id)
    collectChatList(lounge.id)

    currentJob = launch {
      val member = lounge.members.find { it.userId == currentState.user.id }
        ?: throw LoungeError.InvalidMember
      checkMemberStatus(member)
    }
  }

  private suspend fun collectChatList(loungeId: String) {
    getChatListUseCase(loungeId)
      .collectLatest { chatList ->
        updateState(LoungeReduce.UpdateChatList(chatList.map { it.asUI() }))
      }
  }

  private suspend fun collectMemberList(loungeId: String) {
    getMemberListUseCase(loungeId)
      .collectLatest { memberList ->
        updateState(LoungeReduce.UpdateMemberList(memberList.map { it.asUI() }))
      }
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    getLoungeStatusUseCase(loungeId)
      .collectLatest { status ->
        if (status == LoungeStatusType.STARTED) {
          sendSideEffect(LoungeSideEffect.NavigateToVote(loungeId))
        }
      }
  }

  private suspend fun checkMemberStatus(member: MemberUIModel) {
    checkMemberStatusUseCase(member.asDomain())
      .collectLatest { status ->
        if (status == MemberStatusType.EXILED) {
          exitLoungeUseCase(member.asDomain())

          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("방장에 의해 추방되었습니다.")))
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

  private suspend fun updateReady() {
    val member = currentState.memberList.find { it.userId == currentState.user.id }
      ?: throw LoungeError.InvalidMember
    updateReadyUseCase(member.asDomain())
  }

  private suspend fun exitLounge() {
    currentJob?.cancel()

    val member = currentState.memberList.find { it.userId == currentState.user.id }
      ?: throw LoungeError.InvalidMember
    exitLoungeUseCase(member.asDomain())

    sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString("투표 대기방에서 나왔습니다.")))
    sendSideEffect(LoungeSideEffect.CloseDialog)
    sendSideEffect(LoungeSideEffect.PopBackStack)
  }

  companion object {
    private const val TIMEOUT = 10000L
  }
}