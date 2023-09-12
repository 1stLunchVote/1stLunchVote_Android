package com.jwd.lunchvote.ui.lounge.member

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.type.MemberStatusType
import com.jwd.lunchvote.domain.usecase.lounge.CheckMemberStatusUseCase
import com.jwd.lunchvote.ui.lounge.member.LoungeMemberContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoungeMemberViewModel @Inject constructor(
    auth: FirebaseAuth,
    checkMemberStatusUseCase: CheckMemberStatusUseCase,
    savedStateHandle: SavedStateHandle,
): BaseStateViewModel<LoungeMemberState, LoungeMemberEvent, LoungeMemberReduce, LoungeMemberSideEffect, LoungeMemberDialogState>(savedStateHandle){
    private val memberId = checkNotNull(savedStateHandle.get<String?>(MEMBER_EXTRA_KEY))
    private val loungeId = checkNotNull(savedStateHandle.get<String?>(LOUNGE_EXTRA_KEY))
    private val nickname = checkNotNull(savedStateHandle.get<String?>(NICKNAME_EXTRA_KEY))
    private val profileUrl = savedStateHandle.get<String?>(PROFILE_URL_EXTRA_KEY)
    private val isOwner = savedStateHandle[IS_OWNER_EXTRA_KEY] ?: false

    override fun createInitialState(savedState: Parcelable?): LoungeMemberState {
        return savedState as? LoungeMemberState ?: LoungeMemberState()
    }

    init {
        updateState(LoungeMemberReduce.SetMemberInfo(memberId, nickname, profileUrl, isOwner))

        checkMemberStatusUseCase(auth.currentUser?.uid!!, loungeId)
            .onEach {
                when(it){
                    MemberStatusType.EXILED, MemberStatusType.EXITED ->{
                        sendSideEffect(LoungeMemberSideEffect.PopBackStack)
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    override fun reduceState(
        state: LoungeMemberState,
        reduce: LoungeMemberReduce,
    ): LoungeMemberState {
        return when(reduce){
            is LoungeMemberReduce.SetMemberInfo -> {
                state.copy(
                    memberId = reduce.memberId,
                    nickname = reduce.nickname,
                    profileUrl = reduce.profileUrl,
                    isOwner = reduce.isOwner
                )
            }
        }
    }

    override fun handleEvents(event: LoungeMemberEvent) {
        when(event){
            is LoungeMemberEvent.OnClickExile -> {

            }
        }
    }

    companion object{
        private const val MEMBER_EXTRA_KEY = "id"
        private const val LOUNGE_EXTRA_KEY = "loungeId"
        private const val NICKNAME_EXTRA_KEY = "nickname"
        private const val PROFILE_URL_EXTRA_KEY = "profileUrl"
        private const val IS_OWNER_EXTRA_KEY = "isOwner"
    }
}