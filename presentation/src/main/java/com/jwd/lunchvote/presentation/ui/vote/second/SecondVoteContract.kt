package com.jwd.lunchvote.presentation.ui.vote.second

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.SecondVoteTileUIModel
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
        data class OnClickVote(val index: Int) : SecondVoteEvent
        data object OnClickFab : SecondVoteEvent
        data object OnTryExit : SecondVoteEvent
        data class OnClickExitDialog(val isExit: Boolean): SecondVoteEvent
    }

    sealed interface SecondVoteReduce : ViewModelContract.Reduce {
        data class SetVoteList(val voteList: List<SecondVoteTileUIModel>) : SecondVoteReduce
        data class ChangeVoted(val index: Int) : SecondVoteReduce
        data object SetVoteCompleted : SecondVoteReduce
        data class SetExitDialogShown(val isShown: Boolean) : SecondVoteReduce
    }

    sealed interface SecondVoteSideEffect : ViewModelContract.SideEffect {
        data class ShowSnackBar(val message: String) : SecondVoteSideEffect
        data object PopBackStack : SecondVoteSideEffect
    }
}