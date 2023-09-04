package com.jwd.lunchvote.ui.vote.second

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.model.SecondVoteTileUIModel
import com.jwd.lunchvote.ui.vote.second.SecondVoteContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class SecondVoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<SecondVoteState, SecondVoteEvent, SecondVoteReduce, SecondVoteSideEffect, SecondVoteDialogState>(savedStateHandle){

    override fun createInitialState(savedState: Parcelable?): SecondVoteState {
        return savedState as? SecondVoteState
            ?: SecondVoteState(nickname = FirebaseAuth.getInstance().currentUser?.displayName)
    }

    init {
        // Todo : 음식 로드 바꾸기
        updateState(SecondVoteReduce.SetVoteList(
            voteList = listOf(
                SecondVoteTileUIModel("https://news.kbs.co.kr/data/news/2017/01/04/3405677_bH6.jpg", "닭강정"),
                SecondVoteTileUIModel("https://news.kbs.co.kr/data/news/2017/01/04/3405677_bH6.jpg", "맛있겠다")
            )
        ))
    }

    override fun handleEvents(event: SecondVoteEvent) {
        when(event){
            is SecondVoteEvent.OnClickVote -> {
                updateState(SecondVoteReduce.ChangeVoted(event.index))
            }
            is SecondVoteEvent.OnClickFab -> {
                updateState(SecondVoteReduce.SetVoteCompleted)
            }
            is SecondVoteEvent.OnTryExit -> {
                updateState(SecondVoteReduce.SetExitDialogShown(true))
            }
            is SecondVoteEvent.OnClickExitDialog -> {
                updateState(SecondVoteReduce.SetExitDialogShown(false))
                if (event.isExit) {
                    sendSideEffect(SecondVoteSideEffect.PopBackStack)
                }
            }
        }
    }


    override fun reduceState(state: SecondVoteState, reduce: SecondVoteReduce): SecondVoteState {
        return when(reduce){
            is SecondVoteReduce.SetVoteList -> state.copy(
                voteList = reduce.voteList
            )
            is SecondVoteReduce.ChangeVoted -> state.copy(
                voteIndex = if (state.voteIndex == reduce.index) -1 else reduce.index,
            )
            is SecondVoteReduce.SetVoteCompleted -> state.copy(
                voteCompleted = !state.voteCompleted
            )
            is SecondVoteReduce.SetExitDialogShown -> state.copy(
                exitDialogShown = reduce.isShown
            )
        }
    }
}