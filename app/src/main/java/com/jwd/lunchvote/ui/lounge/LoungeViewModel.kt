package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.ExitLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.JoinLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.SendChatUseCase
import com.jwd.lunchvote.domain.usecase.lounge.UpdateReadyUseCase
import com.jwd.lunchvote.model.MemberUIModel
import com.jwd.lunchvote.model.mapper.LoungeMapper
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
    private val createLoungeUseCase: CreateLoungeUseCase,
    private val joinLoungeUseCase: JoinLoungeUseCase,
    private val getMemberListUseCase: GetMemberListUseCase,
    private val getChatListUseCase: GetChatListUseCase,
    private val updateReadyUseCase: UpdateReadyUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val exitLoungeUseCase: ExitLoungeUseCase,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<LoungeState, LoungeEvent, LoungeReduce, LoungeSideEffect>(savedStateHandle) {
    private val loungeId = savedStateHandle.get<String?>("id")

    override fun createInitialState(savedState: Parcelable?): LoungeState {
        return savedState as? LoungeState ?: LoungeState()
    }

    init {
        initLounge()
    }

    private fun initLounge(){
        loungeId?.let {
            updateState(LoungeReduce.SetLoungeId(it, false))
            getLoungeData(it)
            joinLounge(it)
        } ?: run {
            createLounge()
        }
    }

    private fun joinLounge(loungeId: String){
        // Todo : JoinLounge는 Navigation 중으로 변경
        joinLoungeUseCase(loungeId)
            .launchIn(viewModelScope)
    }

    private fun createLounge(){
        createLoungeUseCase()
            .onEach {
                updateState(LoungeReduce.SetLoungeId(it, true))
                getLoungeData(it)
            }
            .launchIn(viewModelScope)
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

                // Todo : Owner가 레디가 되면 다음 화면으로 넘어가기
                updateState(LoungeReduce.SetMemberList(it.map { m ->
                    LoungeMapper.mapToMember(m, m.uid == auth.currentUser?.uid) }
                ))
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
        // Todo : 다이얼로그 보여준 후 나가기 -> 서버, 로컬 다 나가기
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
                if (event.exit){
                    viewModelScope.launch {
                        exitLoungeUseCase(
                            auth.currentUser?.uid ?: return@launch,
                            currentState.loungeId ?: return@launch
                        ).catch {

                        }.collect()
                        updateState(LoungeReduce.SetExitDialogShown(false))
                        sendSideEffect(LoungeSideEffect.PopBackStack("투표 대기방에서 나왔습니다."))
                    }
                } else {
                    updateState(LoungeReduce.SetExitDialogShown(false))
                }
            }
        }
    }

    override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
        return when(reduce){
            is LoungeReduce.SetLoungeId -> state.copy(loungeId = reduce.loungeId, isOwner = reduce.isOwner)
            is LoungeReduce.SetMemberList -> {
                state.copy(
                    memberList = reduce.memberList,
                    isReady = reduce.memberList.find { it.isMine }?.isReady ?: false,
                )
            }
            is LoungeReduce.SetChatList -> state.copy(chatList = reduce.chatList.reversed())
            is LoungeReduce.SetCurrentChat -> state.copy(currentChat = reduce.chat)
            is LoungeReduce.SetExitDialogShown -> state.copy(exitDialogShown = reduce.shown)
        }
    }
}