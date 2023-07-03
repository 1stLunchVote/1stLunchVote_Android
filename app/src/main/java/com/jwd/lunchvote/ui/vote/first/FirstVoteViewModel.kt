package com.jwd.lunchvote.ui.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteSideEffect

class FirstVoteViewModel (
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect>(savedStateHandle) {
    override fun createInitialState(savedState: Parcelable?): FirstVoteState =
        savedState as? FirstVoteState ?: FirstVoteState()

    override fun handleEvents(event: FirstVoteEvent) {
        when(event) {
            is FirstVoteEvent.OnClickFood -> {
                when(event.food.status) {
                    FoodStatus.DEFAULT -> updateState(FirstVoteReduce.AddFoodIntoLikeList(event.food))
                    FoodStatus.LIKE -> {
                        updateState(FirstVoteReduce.DeleteFoodFromLikeList(event.food))
                        updateState(FirstVoteReduce.AddFoodIntoDislikeList(event.food))
                    }
                    FoodStatus.DISLIKE -> updateState(FirstVoteReduce.DeleteFoodFromDislikeList(event.food))
                }
            }
            is FirstVoteEvent.TypeSearchKeyword -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
            is FirstVoteEvent.OnClickFinishButton -> updateState(FirstVoteReduce.SetIsFinished(true))
        }
    }

    override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
        return when(reduce) {
            is FirstVoteReduce.AddFoodIntoLikeList -> state.copy(
                likeList = state.likeList + reduce.food
            )
            is FirstVoteReduce.DeleteFoodFromLikeList -> state.copy(
                likeList = state.likeList.filter { it != reduce.food }
            )
            is FirstVoteReduce.AddFoodIntoDislikeList -> state.copy(
                dislikeList = state.dislikeList + reduce.food
            )
            is FirstVoteReduce.DeleteFoodFromDislikeList -> state.copy(
                dislikeList = state.dislikeList.filter { it != reduce.food }
            )
            is FirstVoteReduce.SetTotalMember -> state.copy(totalMember = reduce.totalMember)
            is FirstVoteReduce.UpdateEndedMember -> state.copy(endedMember = reduce.endedMember)
            is FirstVoteReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
            is FirstVoteReduce.SetIsFinished -> state.copy(isFinished = reduce.isFinished)
        }
    }

}