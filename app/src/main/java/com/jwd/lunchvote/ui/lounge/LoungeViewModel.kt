package com.jwd.lunchvote.ui.lounge

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.lounge.CreateLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetMemberListUseCase
import com.jwd.lunchvote.model.LoungeMember
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@HiltViewModel
class LoungeViewModel @Inject constructor(
    private val createLoungeUseCase: CreateLoungeUseCase,
    private val getMemberListUseCase: GetMemberListUseCase,
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<LoungeState, LoungeEvent, LoungeReduce, LoungeSideEffect>(savedStateHandle) {
    private val loungeId = savedStateHandle.get<String?>("id")

    override fun createInitialState(savedState: Parcelable?): LoungeState {
        return savedState as? LoungeState ?: LoungeState()
    }

    init {
        initialize()
    }

    fun initialize(){
        loungeId?.let {
            updateState(LoungeReduce.SetLoungeId(it))
            getMemberList(it)
        } ?: run {
            createLounge()
        }
    }

    private fun createLounge(){
        createLoungeUseCase()
            .onEach {
                updateState(LoungeReduce.SetLoungeId(it))
                getMemberList(it)
            }
            .launchIn(viewModelScope)
    }

    private fun getMemberList(loungeId: String){
        getMemberListUseCase(loungeId)
            .onEach {
                updateState(LoungeReduce.SetMemberList(it.map { m ->
                    LoungeMember(m.profileImage, m.isReady) }
                ))
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvents(event: LoungeEvent) {
        TODO("Not yet implemented")
    }

    override fun reduceState(state: LoungeState, reduce: LoungeReduce): LoungeState {
        return when(reduce){
            is LoungeReduce.SetLoungeId -> state.copy(loungeId = reduce.loungeId)
            is LoungeReduce.SetMemberList -> state.copy(memberList = reduce.memberList)
        }
    }
}