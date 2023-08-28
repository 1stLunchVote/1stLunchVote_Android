package com.jwd.lunchvote.ui.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import timber.log.Timber

class FirstVoteViewModel (
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect>(savedStateHandle) {
    override fun createInitialState(savedState: Parcelable?): FirstVoteState =
        savedState as? FirstVoteState ?: FirstVoteState()

    init {
      updateState(FirstVoteReduce.UpdateFoodList(
          List(20) {
              FoodUIModel(
                  id = "$it",
                  imageUrl = "",
                  name = "음식명",
                  status = FoodStatus.DEFAULT
              )
          }
      ))
    }

    override fun handleEvents(event: FirstVoteEvent) {
        when(event) {
            is FirstVoteEvent.OnClickFood -> updateState(FirstVoteReduce.UpdateFoodStatus(event.food))
            is FirstVoteEvent.SetSearchKeyword -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
            is FirstVoteEvent.OnClickFinishButton -> updateState(FirstVoteReduce.UpdateIsFinished(true))
            is FirstVoteEvent.OnTryExit -> updateState(FirstVoteReduce.UpdateVoteExitDialogShown(true))
            is FirstVoteEvent.OnClickExitDialog -> {
                if(event.isExit) {
                    updateState(FirstVoteReduce.UpdateVoteExitDialogShown(false))
                    sendSideEffect(FirstVoteSideEffect.PopBackStack)
                } else {
                    updateState(FirstVoteReduce.UpdateVoteExitDialogShown(false))
                }
            }
        }
    }

    override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
        return when(reduce) {
            is FirstVoteReduce.UpdateFoodList -> state.copy(foodList = reduce.foodList)
            is FirstVoteReduce.UpdateFoodStatus -> {
                when(reduce.food.status) {
                    FoodStatus.DEFAULT -> state.copy(
                        likeList = state.likeList + reduce.food.copy(status = FoodStatus.LIKE),
                        foodList = state.foodList.map {
                            if (it == reduce.food) it.copy(status = FoodStatus.LIKE) else it
                        }
                    )
                    FoodStatus.LIKE -> state.copy(
                        likeList = state.likeList.filter { it != reduce.food },
                        dislikeList = state.dislikeList + reduce.food.copy(status = FoodStatus.DISLIKE),
                        foodList = state.foodList.map {
                            if (it == reduce.food) it.copy(status = FoodStatus.DISLIKE) else it
                        }
                    )
                    FoodStatus.DISLIKE -> state.copy(
                        dislikeList = state.dislikeList.filter { it != reduce.food },
                        foodList = state.foodList.map {
                            if (it == reduce.food) it.copy(status = FoodStatus.DEFAULT) else it
                        }
                    )
                }
            }
            is FirstVoteReduce.UpdateTotalMember -> state.copy(totalMember = reduce.totalMember)
            is FirstVoteReduce.UpdateEndedMember -> state.copy(endedMember = reduce.endedMember)
            is FirstVoteReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
            is FirstVoteReduce.UpdateIsFinished -> state.copy(isFinished = reduce.isFinished)
            is FirstVoteReduce.UpdateVoteExitDialogShown -> state.copy(voteExitDialogShown = reduce.voteExitDialogShown)
        }
    }

}