package com.jwd.lunchvote.presentation.ui.vote.first

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.first_vote.GetFoodListUseCase
import com.jwd.lunchvote.domain.usecase.first_vote.GetTemplateListUseCase
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteEvent
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteReduce
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteSideEffect
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteContract.FirstVoteState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstVoteViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val getFoodListUseCase: GetFoodListUseCase,
  private val getTemplateListUseCase: GetTemplateListUseCase
): BaseStateViewModel<FirstVoteState, FirstVoteEvent, FirstVoteReduce, FirstVoteSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): FirstVoteState =
    savedState as? FirstVoteState ?: FirstVoteState()

  init {
    val loungeId = checkNotNull(savedStateHandle.get<String>("loungeId"))
    sendEvent(FirstVoteEvent.StartInitialize(loungeId))
  }

  override fun handleEvents(event: FirstVoteEvent) {
    when(event) {
      is FirstVoteEvent.StartInitialize -> viewModelScope.launch { initialize(event.loungeId) }
      is FirstVoteEvent.OnClickFood -> updateState(FirstVoteReduce.UpdateFoodStatus(event.food))
      is FirstVoteEvent.SetSearchKeyword -> updateState(FirstVoteReduce.UpdateSearchKeyword(event.searchKeyword))
      is FirstVoteEvent.OnClickFinishButton -> launch(false) { throw NotImplementedError() }
      is FirstVoteEvent.OnClickExitButton -> launch(false) { throw NotImplementedError() }
    }
  }

  override fun reduceState(state: FirstVoteState, reduce: FirstVoteReduce): FirstVoteState {
    return when(reduce) {
      is FirstVoteReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is FirstVoteReduce.Initialize -> reduce.state
      is FirstVoteReduce.UpdateFoodStatus -> when (reduce.food) {
        in state.likeList -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          likeList = state.likeList.filter { it.id != reduce.food.id },
          dislikeList = state.dislikeList + reduce.food
        )
        in state.dislikeList -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          dislikeList = state.dislikeList.filter { it.id != reduce.food.id }
        )
        else -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          likeList = state.likeList + reduce.food
        )
      }
      is FirstVoteReduce.UpdateTotalMember -> state.copy(totalMember = reduce.totalMember)
      is FirstVoteReduce.UpdateEndedMember -> state.copy(endedMember = reduce.endedMember)
      is FirstVoteReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is FirstVoteReduce.UpdateFinished -> state.copy(finished = reduce.finished)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(FirstVoteSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize(loungeId: String) {
    updateState(FirstVoteReduce.UpdateLoading(true))

    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    val foodList = getFoodListUseCase.invoke()
    val templateList = getTemplateListUseCase.invoke(userId)

    updateState(
      FirstVoteReduce.Initialize(
        FirstVoteState(
          foodMap = foodList.associate { FoodUIModel(it) to FoodStatus.DEFAULT },
          likeList = emptyList(),
          dislikeList = emptyList(),
          totalMember = 3,
          endedMember = 1
        )
      )
    )

//    toggleDialog(
//      FirstVoteDialogState.SelectTemplateDialog(
//        templateList = templateList.map { TemplateUIModel(it) },
//        selectTemplate = { template ->
//          updateState(
//            FirstVoteReduce.Initialize(
//              FirstVoteState(
//                loading = false,
//                foodMap = foodList.associate {
//                  FoodUIModel(it) to when (it.name) {
//                    in template?.like ?: emptyList() -> FoodStatus.LIKE
//                    in template?.dislike ?: emptyList() -> FoodStatus.DISLIKE
//                    else -> FoodStatus.DEFAULT
//                  }
//                },
//                likeList = foodList.filter { (template?.like ?: emptyList()).contains(it.name) }.map { FoodUIModel(it) },
//                dislikeList = foodList.filter { (template?.dislike ?: emptyList()).contains(it.name) }.map { FoodUIModel(it) },
//                totalMember = 3,
//                endedMember = 1
//              )
//            )
//          )
//        }
//      )
//    )
  }
}