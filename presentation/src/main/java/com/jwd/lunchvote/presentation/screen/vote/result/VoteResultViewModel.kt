package com.jwd.lunchvote.presentation.screen.vote.result

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.VoteResultRepository
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultEvent
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultReduce
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultSideEffect
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultContract.VoteResultState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.RouteError
import javax.inject.Inject

@HiltViewModel
class VoteResultViewModel @Inject constructor(
  private val voteResultRepository: VoteResultRepository,
  private val foodRepository: FoodRepository,
  savedStateHandle: SavedStateHandle
) : BaseStateViewModel<VoteResultState, VoteResultEvent, VoteResultReduce, VoteResultSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): VoteResultState {
    return savedState as? VoteResultState ?: VoteResultState()
  }

  private val loungeId: String =
    savedStateHandle[LunchVoteNavRoute.VoteResult.arguments.first().name] ?: throw RouteError.NoArguments

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
    sendSideEffect(VoteResultSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val voteResult = voteResultRepository.getSecondVoteResultByLoungeId(loungeId)
    val food = foodRepository.getFoodById(voteResult.foodId).asUI()

    updateState(VoteResultReduce.UpdateFood(food))
    updateState(VoteResultReduce.UpdateVoteRatio(voteResult.voteRatio))
  }
}