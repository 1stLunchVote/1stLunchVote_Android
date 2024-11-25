package com.jwd.lunchvote.presentation.screen.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Lounge.Status.FIRST_VOTE
import com.jwd.lunchvote.domain.entity.Lounge.Status.QUIT
import com.jwd.lunchvote.domain.entity.Member.Type.EXILED
import com.jwd.lunchvote.domain.entity.Member.Type.LEAVED
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
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.MemberUIModel.Type.DEFAULT
import com.jwd.lunchvote.presentation.model.MemberUIModel.Type.OWNER
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.ExitDialogEvent
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.ExitDialogState
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeEvent
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeReduce
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeSideEffect
import com.jwd.lunchvote.presentation.screen.lounge.LoungeContract.LoungeState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kr.co.inbody.config.error.LoungeError
import kr.co.inbody.config.error.MemberError
import kr.co.inbody.config.error.UserError
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

  private val loungeId: String? =
    savedStateHandle[LunchVoteNavRoute.Lounge.arguments.first().name]

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  private lateinit var loungeFlow: Job
  private lateinit var membersFlow: Job
  private lateinit var memberTypeFlow: Job
  private lateinit var chatListFlow: Job

  init {
    launch {
      val user = userRepository.getUserById(userId).asUI()
      updateState(LoungeReduce.UpdateUser(user))

      val lounge = loungeId?.let { joinLounge(userId, it).asUI() } ?: createLounge(userId).asUI()
      updateState(LoungeReduce.UpdateLounge(lounge))

      collectLoungeData(lounge.id)
    }
  }

  override fun handleEvents(event: LoungeEvent) {
    when (event) {
      is LoungeEvent.OnClickBackButton -> updateState(LoungeReduce.UpdateExitDialogState(ExitDialogState))
      is LoungeEvent.OnClickSettingButton -> sendSideEffect(LoungeSideEffect.NavigateToLoungeSetting(currentState.lounge.id))
      is LoungeEvent.OnClickMember -> sendSideEffect(LoungeSideEffect.NavigateToMember(event.member.userId, currentState.lounge.id))
      is LoungeEvent.OnClickInviteButton -> sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.lounge.id))
      is LoungeEvent.OnTextChange -> updateState(LoungeReduce.UpdateText(event.text))
      is LoungeEvent.OnClickSendChatButton -> launch(false) { sendChat() }
      is LoungeEvent.OnClickActionButton -> launch(false) {
        if (currentState.isOwner) startVote() else updateReady()
      }

      is ExitDialogEvent -> handleExitDialogEvents(event)
    }
  }

  private fun handleExitDialogEvents(event: ExitDialogEvent) {
    when (event) {
      is ExitDialogEvent.OnClickCancelButton -> updateState(LoungeReduce.UpdateExitDialogState(null))
      is ExitDialogEvent.OnClickExitButton -> launch(false) { exitLounge() }
    }
  }

  override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
    return when (reduce) {
      is LoungeReduce.UpdateUser -> state.copy(user = reduce.user)
      is LoungeReduce.UpdateLounge -> state.copy(lounge = reduce.lounge)
      is LoungeReduce.UpdateMemberList -> state.copy(memberList = reduce.memberList)
      is LoungeReduce.UpdateMemberArchive -> state.copy(memberArchive = reduce.memberArchive)
      is LoungeReduce.UpdateIsOwner -> state.copy(isOwner = reduce.isOwner)
      is LoungeReduce.UpdateChatList -> state.copy(chatList = reduce.chatList)
      is LoungeReduce.UpdateText -> state.copy(text = reduce.text)
      is LoungeReduce.UpdateExitDialogState -> state.copy(exitDialogState = reduce.exitDialogState)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.ErrorString(error)))
    when (error) {
      is LoungeError.LoungeStarted -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.LoungeFinished -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.LoungeQuit -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.FullMember -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.NoLounge -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.JoinLoungeFailed -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is LoungeError.ExiledMember -> sendSideEffect(LoungeSideEffect.PopBackStack)
      is MemberError.InvalidMember -> sendSideEffect(LoungeSideEffect.PopBackStack)
      else -> Unit
    }
  }

  private fun collectLoungeData(loungeId: String) {
    loungeFlow = launch(false) { collectLounge(loungeId) }
    membersFlow = launch(false) { collectMembers(loungeId) }
    memberTypeFlow = launch(false) { collectMemberType(loungeId) }
    chatListFlow = launch(false) { collectChatList(loungeId) }
  }

  private suspend fun collectLounge(loungeId: String) {
    loungeRepository.getLoungeFlowById(loungeId).collectLatest { lounge ->
      updateState(LoungeReduce.UpdateLounge(lounge.asUI()))

      when (lounge.status) {
        QUIT -> {
          userStatusRepository.setUserLounge(userId, null)

          sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.StringResource(R.string.lounge_owner_exited_snackbar)))
          sendSideEffect(LoungeSideEffect.PopBackStack)
        }
        FIRST_VOTE -> {
          sendSideEffect(LoungeSideEffect.NavigateToVote(loungeId))
        }
        else -> Unit
      }
    }
  }

  private suspend fun collectMembers(loungeId: String) {
    memberRepository.getMemberArchiveFlow(loungeId).collectLatest { archive ->
      val memberList = archive.filter { it.type !in listOf(LEAVED, EXILED) }.map { it.asUI() }
      val memberArchive = archive.map { it.asUI() }
      val isOwner = memberArchive.any { it.userId == userId && it.type == OWNER }

      updateState(LoungeReduce.UpdateMemberList(memberList))
      updateState(LoungeReduce.UpdateMemberArchive(memberArchive))
      updateState(LoungeReduce.UpdateIsOwner(isOwner))
    }
  }

  private suspend fun collectMemberType(loungeId: String) {
    memberRepository.getMemberTypeFlow(loungeId, userId).collectLatest { type ->
      if (type == EXILED) {
        userStatusRepository.setUserLounge(userId, null)

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

    chatRepository.sendChat(
      chat = Chat.Builder(currentState.lounge.id)
        .setUserId(userId)
        .setMessage(currentState.text)
        .build()
    )
  }

  private suspend fun startVote() {
    if (currentState.memberList.any { it.type == DEFAULT }) {
      sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.StringResource(R.string.lounge_not_ready_to_start_snackbar)))
    } else if (currentState.memberArchive.size <= 1) {
      sendSideEffect(LoungeSideEffect.ShowSnackbar(UiText.StringResource(R.string.lounge_lack_member_snackbar)))
    } else {
      startFirstVote(currentState.lounge.id)
    }
  }

  private suspend fun updateReady() {
    val me = memberRepository.getMember(userId, currentState.lounge.id) ?: throw MemberError.InvalidMember
    memberRepository.updateMemberReadyType(me)
  }

  private suspend fun exitLounge() {
    currentState.exitDialogState ?: return
    updateState(LoungeReduce.UpdateExitDialogState(null))

    loungeFlow.cancel()
    membersFlow.cancel()
    memberTypeFlow.cancel()
    chatListFlow.cancel()

    exitLounge(userId)

    sendSideEffect(LoungeSideEffect.PopBackStack)
  }
}