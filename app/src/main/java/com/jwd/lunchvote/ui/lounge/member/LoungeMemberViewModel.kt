package com.jwd.lunchvote.ui.lounge.member

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.lounge.member.LoungeMemberContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoungeMemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<LoungeMemberState, LoungeMemberEvent, LoungeMemberReduce, LoungeMemberSideEffect>(savedStateHandle){
    private val memberId = checkNotNull(savedStateHandle.get<String?>("id"))
    private val nickname = checkNotNull(savedStateHandle.get<String?>("nickname"))
    private val profileUrl = savedStateHandle.get<String?>("profileUrl")
    private val isOwner = savedStateHandle["isOwner"] ?: false

    override fun createInitialState(savedState: Parcelable?): LoungeMemberState {
        return savedState as? LoungeMemberState ?: LoungeMemberState()
    }

    init {
        updateState(LoungeMemberReduce.SetMemberInfo(memberId, nickname, profileUrl, isOwner))
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
        TODO("Not yet implemented")
    }
}