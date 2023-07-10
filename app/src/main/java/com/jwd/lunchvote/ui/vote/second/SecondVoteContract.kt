package com.jwd.lunchvote.ui.vote.second

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.SecondVoteTileUIModel
import kotlinx.parcelize.Parcelize

class SecondVoteContract {
    @Parcelize
    data class SecondVoteState(
        val nickname: String? = null,
        val voteList : List<SecondVoteTileUIModel> = emptyList(),
        val voteIndex : Int = -1,
        val voteCnt : Int = 0,
        val voteCompleted : Boolean = false,
    ) : ViewModelContract.State, Parcelable{
        override fun toParcelable(): Parcelable = this
    }

    sealed interface SecondVoteEvent : ViewModelContract.Event {
        class OnClickVote(val index: Int) : SecondVoteEvent
        object OnClickFab : SecondVoteEvent
    }

    sealed interface SecondVoteReduce : ViewModelContract.Reduce {
        data class SetVoteList(val voteList: List<SecondVoteTileUIModel>) : SecondVoteReduce
        data class ChangeVoted(val index: Int) : SecondVoteReduce
        class SetVoteCompleted : SecondVoteReduce
    }

    sealed interface SecondVoteSideEffect : ViewModelContract.SideEffect {
        data class ShowSnackBar(val message: String) : SecondVoteSideEffect
    }
}