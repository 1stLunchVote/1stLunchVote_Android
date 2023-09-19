package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
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
import com.jwd.lunchvote.mapper.LoungeMapper
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
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
): BaseStateViewModel<LoungeState, LoungeEvent, LoungeReduce, LoungeSideEffect, LoungeDialogState>(savedStateHandle) {
    // todo : 이미 시작된 방인 경우 뒤로가기 처리 필요

    private val loungeId = savedStateHandle.get<String?>(LOUNGE_KEY_ID).also {
        updateState(LoungeReduce.SetIsOwner(it == null))
    }

    private var checkJob : Job? = null

    override fun createInitialState(savedState: Parcelable?): LoungeState {
        return savedState as? LoungeState ?: LoungeState()
    }

    init {
        initLounge()
    }

    private fun initLounge(){
        loungeId?.let {
            joinLounge(it)
            getLoungeData(it)
        } ?: run {
            createLounge()
        }
    }

    private fun createLounge(){
        launch {
            withTimeoutOrNull(TIMEOUT){
                val res = createLoungeUseCase()

                updateState(LoungeReduce.SetLoungeId(res))

                getLoungeData(res)
                checkMemberStatus()
            } ?: run {
                sendSideEffect(LoungeSideEffect.PopBackStack("방 생성에 실패하였습니다."))
            }
        }
    }

    private fun joinLounge(loungeId: String){
        launch {
            withTimeoutOrNull(TIMEOUT){
                joinLoungeUseCase(loungeId)

                updateState(LoungeReduce.SetLoungeId(loungeId))

                checkMemberStatus()
            } ?: run {
                sendSideEffect(LoungeSideEffect.PopBackStack("입장에 실패하였습니다."))
            }
        }
    }

    private fun sendChat(){
        launch {
            sendChatUseCase(currentState.loungeId ?: return@launch, currentState.currentChat)
        }
    }

    private fun getLoungeData(loungeId: String){
        launch {
            getChatListUseCase(loungeId)
                .collectLatest {
                    updateState(
                        LoungeReduce.SetChatList(it.map { chat ->
                            LoungeMapper.mapToChat(chat, chat.userId == auth.currentUser?.uid) }
                        )
                    )
                }
        }


        launch {
            getMemberListUseCase(loungeId)
                .collectLatest {
                    updateState(LoungeReduce.SetMemberList(it.map { m ->
                        LoungeMapper.mapToMember(m, m.id == auth.currentUser?.uid) }
                    ))
                }
        }


        launch {
            getLoungeStatusUseCase(loungeId)
                .collectLatest {
                    when(it){
                        LoungeStatusType.STARTED -> {
                            sendSideEffect(LoungeSideEffect.NavigateToVote(loungeId))
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun checkMemberStatus(){
        checkJob = launch {
            checkMemberStatusUseCase(auth.currentUser?.uid ?: return@launch, currentState.loungeId ?: return@launch)
                .collectLatest {
                    when(it){
                        MemberStatusType.EXITED -> {
                            sendSideEffect(LoungeSideEffect.PopBackStack("방장이 방을 종료하였습니다."))
                        }
                        MemberStatusType.EXILED -> {
                            sendSideEffect(LoungeSideEffect.PopBackStack("방장에 의해 추방되었습니다."))

                            CoroutineScope(Dispatchers.IO).launch{
                                checkJob?.cancel()

                                currentState.loungeId?.let {
                                    exitLoungeUseCase(auth.currentUser?.uid ?: return@launch, it)
                                }
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun updateReady(){
        launch {
            runCatching {
                updateReadyUseCase(
                    auth.currentUser?.uid ?: return@launch,
                    currentState.loungeId ?: return@launch,
                    currentState.isOwner
                )
            }.onFailure {
                sendSideEffect(LoungeSideEffect.ShowSnackBar(
                    if (currentState.isOwner) "게임 시작 실패" else "준비 상태 변경 실패")
                )
            }
        }
    }

    private fun exitLounge(){
        // 대기방에서 먼저 나오고 백그라운드로 라운 나오기
        CoroutineScope(Dispatchers.IO).launch{
            checkJob?.cancel()

            sendSideEffect(LoungeSideEffect.PopBackStack("투표 대기방에서 나왔습니다."))

            currentState.loungeId?.let {
                exitLoungeUseCase(auth.currentUser?.uid ?: return@launch, it)
            }
        }
    }

    override fun handleEvents(event: LoungeEvent) {
        when(event){
            is LoungeEvent.OnEditChat -> updateState(LoungeReduce.SetCurrentChat(event.chat))
            is LoungeEvent.OnSendChat -> {
                updateState(LoungeReduce.SetCurrentChat(""))
                sendChat()
            }
            is LoungeEvent.OnReady -> {
                if (currentState.isOwner && currentState.allReady.not()){
                    sendSideEffect(LoungeSideEffect.ShowSnackBar("멤버 전원이 준비해야합니다."))
                    return
                }
                updateReady()
            }
            is LoungeEvent.OnTryExit -> {
                updateState(LoungeReduce.SetExitDialogShown(true))
            }
            is LoungeEvent.OnClickExit -> {
                if (event.exit){ exitLounge() }

                updateState(LoungeReduce.SetExitDialogShown(false))
            }
            is LoungeEvent.OnClickInvite -> {
                sendSideEffect(LoungeSideEffect.CopyToClipboard(currentState.loungeId ?: return))
            }
            is LoungeEvent.OnScrolled -> {
                updateState(LoungeReduce.SetScrollIndex(event.index))
            }
        }
    }

    override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
        return when(reduce){
            is LoungeReduce.SetIsOwner -> state.copy(isOwner = reduce.isOwner)
            is LoungeReduce.SetLoungeId -> state.copy(loungeId = reduce.loungeId)
            is LoungeReduce.SetMemberList -> {
                state.copy(
                    memberList = reduce.memberList,
                    isReady = reduce.memberList.find { it.isMine }?.isReady ?: false,
                )
            }
            is LoungeReduce.SetChatList -> state.copy(chatList = reduce.chatList.reversed())
            is LoungeReduce.SetCurrentChat -> state.copy(currentChat = reduce.chat)
            is LoungeReduce.SetExitDialogShown -> state.copy(exitDialogShown = reduce.shown)
            is LoungeReduce.SetScrollIndex -> state.copy(scrollIndex = reduce.index)
        }
    }

    companion object{
        private const val TIMEOUT = 10000L
        private const val LOUNGE_KEY_ID = "id"
    }
}