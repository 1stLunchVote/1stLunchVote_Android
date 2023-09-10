package com.jwd.lunchvote.ui.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteDialogState
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class FirstVoteViewModel (
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect, FirstVoteDialogState>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): FirstVoteState =
    savedState as? FirstVoteState ?: FirstVoteState()

  init {
    val loungeId = checkNotNull(savedStateHandle.get<String>("loungeId"))
    sendEvent(FirstVoteEvent.StartInitialize(loungeId))
  }

  override fun handleEvents(event: FirstVoteEvent) {
    when(event) {
      is FirstVoteEvent.StartInitialize -> {
        CoroutineScope(ioDispatcher).launch {
          initialize(event.loungeId)
        }
      }
      is FirstVoteEvent.OnClickFood -> updateState(FirstVoteReduce.UpdateFoodStatus(event.food))
      is FirstVoteEvent.SetSearchKeyword -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
      is FirstVoteEvent.OnClickFinishButton -> toggleDialog(
        FirstVoteDialogState.VoteExitDialogState {
          updateState(FirstVoteReduce.UpdateFinished(true))
        }
      )
      is FirstVoteEvent.OnClickExitButton -> toggleDialog(
        FirstVoteDialogState.VoteExitDialogState {
          sendSideEffect(FirstVoteSideEffect.PopBackStack)
        }
      )
    }
  }

  override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
    return when(reduce) {
      is FirstVoteReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is FirstVoteReduce.Initialize -> reduce.state
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
      is FirstVoteReduce.UpdateFinished -> state.copy(finished = reduce.finished)
    }
  }

  private suspend fun initialize(loungeId: String) {
    val templateList = templateRepository.getTemplateList(loungeId)

    toggleDialog(FirstVoteDialogState.SelectTemplateDialog(emptyList()) {})
  }
}