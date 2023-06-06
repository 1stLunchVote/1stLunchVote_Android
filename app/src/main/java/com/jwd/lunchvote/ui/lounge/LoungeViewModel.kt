package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetChatListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.domain.usecase.lounge.JoinLoungeUseCase
import com.jwd.lunchvote.model.ChatUIModel
import com.jwd.lunchvote.model.MemberUIModel
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
    private val createLoungeUseCase: CreateLoungeUseCase,
    private val joinLoungeUseCase: JoinLoungeUseCase,
    private val getMemberListUseCase: GetMemberListUseCase,
    private val getChatListUseCase: GetChatListUseCase,
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
            joinLounge(it)
        } ?: run {
            createLounge()
        }
    }

    private fun joinLounge(loungeId: String){
        joinLoungeUseCase(loungeId)
            .onEach {
                getLoungeData(loungeId)
            }
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

    private fun getLoungeData(loungeId: String){
        getMemberListUseCase(loungeId)
            .onEach {
                updateState(LoungeReduce.SetMemberList(it.map { m ->
                    MemberUIModel(m.uid.orEmpty(), m.profileImage, m.ready, m.owner) }
                ))
            }
            .launchIn(viewModelScope)

        getChatListUseCase(loungeId)
            .onEach {
                updateState(
                    LoungeReduce.SetChatList(it.map { chat ->
                        ChatUIModel(
                            chat.content.orEmpty(), chat.messageType, chat.sender == auth.currentUser?.uid,
                            chat.sender.orEmpty(), chat.createdAt.orEmpty(), chat.senderProfile
                        )
                        }
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvents(event: LoungeEvent) {
        when(event){
            is LoungeEvent.OnEditChat -> updateState(LoungeReduce.SetCurrentChat(event.chat))
            is LoungeEvent.OnSendChat -> {
                // Todo : 채팅 보내기
            }
        }
    }

    override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
        return when(reduce){
            is LoungeReduce.SetLoungeId -> state.copy(loungeId = reduce.loungeId, isOwner = reduce.isOwner)
            is LoungeReduce.SetMemberList -> state.copy(memberList = reduce.memberList)
            is LoungeReduce.SetChatList -> state.copy(chatList = reduce.chatList)
            is LoungeReduce.SetCurrentChat -> state.copy(currentChat = reduce.chat)
        }
    }
}