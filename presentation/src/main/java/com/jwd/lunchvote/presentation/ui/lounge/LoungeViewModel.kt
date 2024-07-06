package com.jwd.lunchvote.presentation.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.repository.ChatRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.MemberRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.domain.usecase.CreateLounge
import com.jwd.lunchvote.domain.usecase.ExitLounge
import com.jwd.lunchvote.domain.usecase.JoinLounge
import com.jwd.lunchvote.domain.usecase.StartFirstVote
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.model.LoungeUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
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
import kr.co.inbody.config.error.LoungeError
import kr.co.inbody.config.error.MemberError
import kr.co.inbody.config.error.UserError
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val loungeRepository: LoungeRepository,
  private val userStatusRepository: UserStatusRepository,
  private val memberRepository: MemberRepository,
  private val chatRepository: ChatRepository,
  private val createLounge: CreateLounge,
  private val joinLounge: JoinLounge,
  private val exitLounge: ExitLounge,
  private val startFirstVote: StartFirstVote,
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

  private lateinit var loungeStatusFlow: Job
  private lateinit var memberListFlow: Job
  private lateinit var memberTypeFlow: Job
  private lateinit var chatListFlow: Job

  private val owner: MemberUIModel
    get() = currentState.memberList.find { it.type == MemberUIModel.Type.OWNER } ?: throw LoungeError.NoOwner
  private val me: MemberUIModel
    get() = currentState.memberList.find { it.userId == currentState.user.id } ?: throw MemberError.InvalidMember

  init {
    launch {
      val userId = Firebase.auth.currentUser?.uid ?: throw UserError.NoUser
      val user = userRepository.getUserById(userId).asUI()
      updateState(LoungeReduce.UpdateUser(user))

      val loungeIdKey = LunchVoteNavRoute.Lounge.arguments.first().name
      val loungeId = savedStateHandle.get<String?>(loungeIdKey)
      if (loungeId == null) createLounge() else joinLounge(loungeId)
    }
  }

  override fun handleEvents(event: LoungeEvent) {
    when (event) {
      is LoungeEvent.OnClickBackButton -> sendSideEffect(LoungeSideEffect.OpenVoteExitDialog)
      is LoungeEvent.OnClickMember -> sendSideEffect(LoungeSideEffect.NavigateToMember(event.member.userId, currentState.lounge.id))
      is LoungeEvent.OnClickInviteButton -> sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.lounge.id))
      is LoungeEvent.OnTextChange -> updateState(LoungeReduce.UpdateText(event.text))
      is LoungeEvent.OnClickSendChatButton -> launch(false) { sendChat() }
      is LoungeEvent.OnClickActionButton -> launch(false) {
        if (currentState.user.id == owner.userId) startVote() else updateReady()
      }

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
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    when (error) {
      is LoungeError.LoungeQuit -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.FullMember -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.NoLounge -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.JoinLoungeFailed -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is MemberError.InvalidMember -> sendSideEffect(LoungeSideEffect.PopBackStack)
      else -> Unit
    }
  }

  private suspend fun createLounge() {
    withTimeoutOrNull(TIMEOUT) {
      val user = currentState.user
      val loungeId = createLounge(user.asDomain())
      val lounge = loungeRepository.getLoungeById(loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge)

      userStatusRepository.setUserLounge(user.id, loungeId)
    } ?: throw LoungeError.CreateLoungeFailed
  }

  private suspend fun joinLounge(loungeId: String) {
    withTimeoutOrNull(TIMEOUT) {
      val user = currentState.user
      val lounge = joinLounge(user.asDomain(), loungeId).asUI()

      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge)

      userStatusRepository.setUserLounge(user.id, loungeId)
    } ?: throw LoungeError.JoinLoungeFailed
  }

  private fun collectLoungeData(lounge: LoungeUIModel) {
    loungeStatusFlow = launch { collectLoungeStatus(lounge.id) }
    memberListFlow = launch { collectMemberList(lounge.id) }
    memberTypeFlow = launch { collectMemberType(lounge.id) }
    chatListFlow = launch { collectChatList(lounge.id) }
  }

  private suspend fun collectLoungeStatus(loungeId: String) {
    loungeRepository.getLoungeStatusFlowById(loungeId).collectLatest { status ->
      when (status) {
        Lounge.Status.QUIT -> {
          userStatusRepository.setUserLounge(currentState.user.id, null)

          sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.StringResource(R.string.lounge_owner_exited_snackbar)))
          sendSideEffect(LoungeSideEffect.PopBackStack)
        }
        Lounge.Status.FIRST_VOTE -> {
          sendSideEffect(LoungeSideEffect.NavigateToVote(loungeId))
        }
        else -> Unit
      }
    }
  }

  private suspend fun collectMemberList(loungeId: String) {
    memberRepository.getMemberListFlow(loungeId).collectLatest { memberList ->
      updateState(LoungeReduce.UpdateMemberList(memberList.map { it.asUI() }))
    }
  }

  private suspend fun collectMemberType(loungeId: String) {
    memberRepository.getMemberTypeFlow(loungeId, currentState.user.id).collectLatest { type ->
      if (type == Member.Type.EXILED) {
        userStatusRepository.setUserLounge(currentState.user.id, null)

        sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.StringResource(R.string.lounge_exiled_snackbar)))
        sendSideEffect(LoungeSideEffect.PopBackStack)
      }
    }
  }

  private suspend fun collectChatList(loungeId: String) {
    chatRepository.getChatListFlow(loungeId).collectLatest { chatList ->
      updateState(LoungeReduce.UpdateChatList(chatList.map { it.asUI() }))
    }
  }

  private suspend fun sendChat() {
    updateState(LoungeReduce.UpdateText(""))

    val chat = ChatUIModel(
      id = UUID.randomUUID().toString(),
      loungeId = currentState.lounge.id,
      userId = currentState.user.id,
      userName = currentState.user.name,
      userProfile = currentState.user.profileImage,
      message = currentState.text,
      type = ChatUIModel.Type.DEFAULT,
      createdAt = ZonedDateTime.now()
    )
    chatRepository.sendChat(chat.asDomain())
  }

  private suspend fun startVote() {
    if (currentState.memberList.any { it.type == MemberUIModel.Type.DEFAULT }) {
      sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.StringResource(R.string.lounge_not_ready_to_start_snackbar)))
    } else {
      startFirstVote(currentState.lounge.id)
    }
  }

  private suspend fun updateReady() {
    memberRepository.updateMemberReadyType(me.asDomain())
  }

  private suspend fun exitLounge() {
    loungeStatusFlow.cancel()
    memberListFlow.cancel()
    memberTypeFlow.cancel()
    chatListFlow.cancel()

    userStatusRepository.setUserLounge(currentState.user.id, null)
    exitLounge(me.asDomain())

    sendSideEffect(LoungeSideEffect.CloseDialog)
    sendSideEffect(LoungeSideEffect.PopBackStack)
  }

  companion object {
    private const val TIMEOUT = 10000L
  }
}