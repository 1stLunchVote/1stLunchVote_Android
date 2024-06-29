package com.jwd.lunchvote.presentation.ui.vote.result

import android.os.Parcelable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.StorageRepository
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
  private val storageRepository: StorageRepository,
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
      is VoteResultReduce.UpdateFoodImageUri -> state.copy(foodImageUri = reduce.foodImageUri)
      is VoteResultReduce.UpdateVoteRatio -> state.copy(voteRatio = reduce.voteRatio)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(VoteResultSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val loungeIdKey = LunchVoteNavRoute.SecondVote.arguments.first().name
    val loungeId = checkNotNull(savedStateHandle.get<String>(loungeIdKey))
    val voteResult = voteResultRepository.getSecondVoteResultByLoungeId(loungeId)
    val food = foodRepository.getFoodById(voteResult.foodId).asUI()
    val foodImageUri = storageRepository.getFoodImageUri(food.name).toUri()

    updateState(VoteResultReduce.UpdateFood(food))
    updateState(VoteResultReduce.UpdateFoodImageUri(foodImageUri))
    updateState(VoteResultReduce.UpdateVoteRatio(voteResult.voteRatio))
  }
}