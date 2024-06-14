package com.jwd.lunchvote.presentation.ui.vote.result

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultEvent
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultReduce
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultSideEffect
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultContract.VoteResultState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VoteResultViewModel @Inject constructor(
  private val voteResultRepository: VoteResultRepository,
  private val foodRepository: FoodRepository,
  private val savedStateHandle: SavedStateHandle
) : BaseStateViewModel<VoteResultState, VoteResultEvent, VoteResultReduce, VoteResultSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): VoteResultState {
    return savedState as? VoteResultState ?: VoteResultState()
  }

  override fun handleEvents(event: VoteResultEvent) {
    when (event) {
      is VoteResultEvent.ScreenInitialize -> launch { initialize() }
      is VoteResultEvent.OnClickHomeButton -> sendSideEffect(VoteResultSideEffect.NavigateToHome)
    }
  }

  override fun reduceState(state: VoteResultState, reduce: VoteResultReduce): VoteResultState {
    return when (reduce) {
      is VoteResultReduce.UpdateFood -> state.copy(food = reduce.food)
      is VoteResultReduce.UpdateVoteRatio -> state.copy(voteRatio = reduce.voteRatio)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(VoteResultSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize() {
    val loungeIdKey = LunchVoteNavRoute.SecondVote.arguments.first().name
    val loungeId = checkNotNull(savedStateHandle.get<String>(loungeIdKey))
    val voteResult = voteResultRepository.getSecondVoteResultByLoungeId(loungeId)
    val food = foodRepository.getFoodById(voteResult.foodId).asUI()

    updateState(VoteResultReduce.UpdateFood(food))
    updateState(VoteResultReduce.UpdateVoteRatio(voteResult.voteRatio))
  }
}