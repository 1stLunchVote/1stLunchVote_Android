package com.jwd.lunchvote.presentation.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.LoungeError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.common.error.UserError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.type.LoungeStatus
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.presentation.R
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
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
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
    get() = currentState.memberList.find { it.status == MemberStatusUIType.OWNER } ?: throw LoungeError.NoOwner

  init {
    launch {
      val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser
      val user = userRepository.getUserById(userId).asUI()
      updateState(LoungeReduce.UpdateUser(user))

      val loungeIdKey = LunchVoteNavRoute.Lounge.arguments.first().name
      val loungeId = savedStateHandle.get<String?>(loungeIdKey)
      if (loungeId != null) joinLounge(loungeId) else createLounge()
    }
  }

  override fun handleEvents(event: LoungeEvent) {
    when (event) {
      is LoungeEvent.OnClickBackButton -> sendSideEffect(LoungeSideEffect.OpenVoteExitDialog)
      is LoungeEvent.OnClickMember -> sendSideEffect(LoungeSideEffect.NavigateToMember(event.member.userId, currentState.lounge.id))
      is LoungeEvent.OnClickInviteButton -> sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.lounge.id))
      is LoungeEvent.OnTextChange -> updateState(LoungeReduce.UpdateText(event.text))
      is LoungeEvent.OnClickSendChatButton -> launch(false) { sendChat() }
      is LoungeEvent.OnClickReadyButton -> launch(false) { updateReady() }
      is LoungeEvent.OnScrolled -> updateState(LoungeReduce.UpdateScrollIndex(event.index))

      // DialogEvents
      is LoungeEvent.OnClickCancelButtonVoteExitDialog -> sendSideEffect(LoungeSideEffect.CloseDialog)
      is LoungeEvent.OnClickConfirmButtonVoteExitDialog -> launch(false) { exitLounge() }
    }
  }

  override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
    return when (reduce) {
      is LoungeReduce.UpdateUser -> state.copy(user = reduce.user)
      is LoungeReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is LoungeReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
      is LoungeReduce.UpdateChatList -> state.copy(chatList = reduce.chatList)
      is LoungeReduce.UpdateText -> state.copy(text = reduce.text)
      is LoungeReduce.UpdateScrollIndex -> state.copy(scrollIndex = reduce.index)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
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
      val loungeId = loungeRepository.createLounge(user.asDomain())
      val lounge = loungeRepository.getLoungeById(loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge)
    } ?: {
      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_create_lounge_snackbar)))
      sendSideEffect(LoungeSideEffect.PopBackStack)
    }
  }

  private suspend fun joinLounge(loungeId: String) {
    withTimeoutOrNull(TIMEOUT) {
      val user = currentState.user
      loungeRepository.getLoungeById(loungeId).asUI().apply {
        if (status != LoungeStatusUIType.CREATED) {
          // TODO: 다양한 투표 방 상태에 따른 스낵바 출력
          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_started_snackbar)))
          sendSideEffect(LoungeSideEffect.PopBackStack)
        }
        if (members == 6) {
          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_full_snackbar)))
          sendSideEffect(LoungeSideEffect.PopBackStack)
        }
      }

      val lounge = loungeRepository.joinLounge(user.asDomain(), loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge)
    } ?: {
      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_join_lounge_snackbar)))
      sendSideEffect(LoungeSideEffect.PopBackStack)
    }
  }

  private fun collectLoungeData(lounge: LoungeUIModel) {
    launch { collectLoungeStatus(lounge.id) }
    launch { collectMemberList(lounge.id) }
    launch { collectChatList(lounge.id) }
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatus(loungeId).collectLatest { status ->
      if (status == LoungeStatus.STARTED) {
        sendSideEffect(LoungeSideEffect.NavigateToVote(loungeId))
      }
    }
  }

  private suspend fun collectMemberList(loungeId: String) {
    loungeRepository.getMemberList(loungeId).collectLatest { memberList ->
      when {
        memberList.find { it.status == MemberStatusType.OWNER } == null -> {
          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_owner_exited_snackbar)))
          sendSideEffect(LoungeSideEffect.PopBackStack)
        }

        memberList.find { it.userId == currentState.user.id } == null -> {
          sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_exiled_snackbar)))
          sendSideEffect(LoungeSideEffect.PopBackStack)
        }

        else -> {
          updateState(LoungeReduce.UpdateMemberList(memberList.map { it.asUI() }))

          val member = memberList.find { it.userId == currentState.user.id }
          currentJob = launch { checkMemberStatus(member!!.asUI()) }
        }
      }
    }
  }

  private suspend fun collectChatList(loungeId: String) {
    loungeRepository.getChatList(loungeId).collectLatest { chatList ->
      updateState(LoungeReduce.UpdateChatList(chatList.map { it.asUI() }))
    }
  }

  private suspend fun checkMemberStatus(member: MemberUIModel) {
    loungeRepository.getMemberStatus(member.asDomain()).collectLatest { status ->
      if (status == MemberStatusType.EXILED) {
        sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_exiled_snackbar)))
        sendSideEffect(LoungeSideEffect.PopBackStack)
      }
    }
  }

  private suspend fun sendChat() {
    updateState(LoungeReduce.UpdateText(""))

    val chat = LoungeChatUIModel(
      id = UUID.randomUUID().toString(),
      loungeId = currentState.lounge.id,
      userId = currentState.user.id,
      userName = currentState.user.name,
      userProfile = currentState.user.profileImage,
      message = currentState.text,
      messageType = MessageUIType.NORMAL,
      sendStatus = SendStatusUIType.SENDING,
      createdAt = ZonedDateTime.now().toString()
    )
    loungeRepository.sendChat(chat.asDomain())
  }

  private suspend fun updateReady() {
    if (currentState.user.id == owner.userId && currentState.memberList.any { it.status != MemberStatusUIType.READY }) {
      sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_not_ready_to_start_snackbar)))
    } else {
      val member = currentState.memberList.find { it.userId == currentState.user.id }
        ?: throw LoungeError.InvalidMember
      loungeRepository.updateReady(member.asDomain())
    }
  }

  private suspend fun exitLounge() {
    currentJob?.cancel()

    val member = currentState.memberList.find { it.userId == currentState.user.id }
      ?: throw LoungeError.InvalidMember
    loungeRepository.exitLounge(member.asDomain())

    sendSideEffect(LoungeSideEffect.ShowSnackBar(UiText.StringResource(R.string.lounge_exited_snackbar)))
    sendSideEffect(LoungeSideEffect.CloseDialog)
    sendSideEffect(LoungeSideEffect.PopBackStack)
  }

  companion object {
    private const val TIMEOUT = 10000L
  }
}