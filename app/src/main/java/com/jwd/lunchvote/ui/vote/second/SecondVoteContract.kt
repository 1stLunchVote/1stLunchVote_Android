package com.jwd.lunchvote.ui.vote.second

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.model.SecondVoteTileUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract
import kotlinx.parcelize.Parcelize

class SecondVoteContract {
    @Parcelize
    data class SecondVoteState(
        val nickname: String? = null,
        val voteList : List<SecondVoteTileUIModel> = emptyList(),
        val voteIndex : Int = -1,
        val voteCnt : Int = 0,
        val voteCompleted : Boolean = false,
        val exitDialogShown : Boolean = false,
    ) : ViewModelContract.State, Parcelable{
        override fun toParcelable(): Parcelable = this
    }

    sealed interface SecondVoteEvent : ViewModelContract.Event {
        class OnClickVote(val index: Int) : SecondVoteEvent
        object OnClickFab : SecondVoteEvent
        object OnTryExit : SecondVoteEvent
        data class OnClickExitDialog(val isExit: Boolean): SecondVoteEvent
    }

    sealed interface SecondVoteReduce : ViewModelContract.Reduce {
        data class SetVoteList(val voteList: List<SecondVoteTileUIModel>) : SecondVoteReduce
        data class ChangeVoted(val index: Int) : SecondVoteReduce
        object SetVoteCompleted : SecondVoteReduce
        data class SetExitDialogShown(val isShown: Boolean) : SecondVoteReduce
    }

    sealed interface SecondVoteSideEffect : ViewModelContract.SideEffect {
        data class ShowSnackBar(val message: String) : SecondVoteSideEffect
        object PopBackStack : SecondVoteSideEffect
    }
}