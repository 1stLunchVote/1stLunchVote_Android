package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.MemberStatus
import com.jwd.lunchvote.domain.usecase.lounge.CheckMemberStatusUseCase
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.ExitLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.JoinLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.SendChatUseCase
import com.jwd.lunchvote.domain.usecase.lounge.UpdateReadyUseCase
import com.jwd.lunchvote.model.mapper.LoungeMapper
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
    private val joinLoungeUseCase: JoinLoungeUseCase,
    private val createLoungeUseCase: CreateLoungeUseCase,
    private val getMemberListUseCase: GetMemberListUseCase,
    private val getChatListUseCase: GetChatListUseCase,
    private val updateReadyUseCase: UpdateReadyUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val exitLoungeUseCase: ExitLoungeUseCase,
    private val checkMemberStatusUseCase: CheckMemberStatusUseCase,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<LoungeState, LoungeEvent, LoungeReduce, LoungeSideEffect>(savedStateHandle) {
    private val loungeId = savedStateHandle.get<String?>("id").also {
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
        viewModelScope.launch {
            withTimeoutOrNull(TIMEOUT){
                val res = createLoungeUseCase().first()

                updateState(LoungeReduce.SetLoungeId(res))
                getLoungeData(res)
            } ?: run {
                sendSideEffect(LoungeSideEffect.PopBackStack("방 생성에 실패하였습니다."))
            }
        }
    }

    private fun joinLounge(loungeId: String){
        viewModelScope.launch {
            withTimeoutOrNull(TIMEOUT){
                joinLoungeUseCase(loungeId).collect()

                updateState(LoungeReduce.SetLoungeId(loungeId))
            } ?: run {
                sendSideEffect(LoungeSideEffect.PopBackStack("입장에 실패하였습니다."))
            }
        }
    }

    private fun sendChat(){
        sendChatUseCase(currentState.loungeId ?: return, currentState.currentChat)
            .launchIn(viewModelScope)
    }

    private fun getLoungeData(loungeId: String){
        getChatListUseCase(loungeId)
            .onEach {
                updateState(
                    LoungeReduce.SetChatList(it.map { chat ->
                        LoungeMapper.mapToChat(chat, chat.sender == auth.currentUser?.uid) }
                    )
                )
            }
            .launchIn(viewModelScope)

        getMemberListUseCase(loungeId)
            .onEach {
                updateState(LoungeReduce.SetMemberList(it.map { m ->
                    LoungeMapper.mapToMember(m, m.uid == auth.currentUser?.uid) }
                ))
            }
            .launchIn(viewModelScope)

        checkJob = checkMemberStatusUseCase(auth.currentUser?.uid ?: return, loungeId)
            .onEach {
                when(it){
                    MemberStatus.EXITED -> {
                        sendSideEffect(LoungeSideEffect.PopBackStack("방장이 방을 종료하였습니다."))
                    }
                    MemberStatus.EXILED -> {
                        sendSideEffect(LoungeSideEffect.PopBackStack("방장에 의해 추방되었습니다."))
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateReady(){
        updateReadyUseCase(auth.currentUser?.uid ?: return, currentState.loungeId ?: return)
            .catch {
                sendSideEffect(LoungeSideEffect.ShowSnackBar(
                    if (currentState.isOwner) "게임 시작 실패" else "준비 상태 변경 실패")
                )
            }
            .onEach {
                if (currentState.isOwner){
                    // Todo : Owner가 레디가 되면 다음 화면으로 넘어가기

                }
            }
            .launchIn(viewModelScope)

    }

    private fun exitLounge(){
        viewModelScope.launch {
            checkJob?.cancel()

            currentState.loungeId?.let {
                exitLoungeUseCase(auth.currentUser?.uid ?: return@launch, it).collect()
            }

            sendSideEffect(LoungeSideEffect.PopBackStack("투표 대기방에서 나왔습니다."))
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
    }
}