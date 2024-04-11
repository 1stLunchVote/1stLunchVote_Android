package com.jwd.lunchvote.presentation.ui.template.add_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.base.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.usecase.template.AddTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.GetFoodListUseCase
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateReduce
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
  private val addTemplateUseCase: AddTemplateUseCase,
  private val getFoodListUseCase: GetFoodListUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<AddTemplateState, AddTemplateEvent, AddTemplateReduce, AddTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): AddTemplateState {
    return savedState as? AddTemplateState ?: AddTemplateState()
  }

  init {
    val templateName = checkNotNull(savedStateHandle.get<String>("templateName"))
    sendEvent(AddTemplateEvent.StartInitialize(templateName))
  }

  override fun handleEvents(event: AddTemplateEvent) {
    when(event) {
      is AddTemplateEvent.StartInitialize -> viewModelScope.launch { initialize(event.templateName) }
      is AddTemplateEvent.OnClickBackButton -> sendSideEffect(AddTemplateSideEffect.PopBackStack)
      is AddTemplateEvent.OnClickFood -> updateState(AddTemplateReduce.UpdateFoodStatus(event.food))
      is AddTemplateEvent.SetSearchKeyword -> updateState(
        AddTemplateReduce.UpdateSearchKeyword(
          event.searchKeyword
        )
      )
      is AddTemplateEvent.OnClickAddButton -> viewModelScope.launch { addTemplate() }
    }
  }

  override fun reduceState(state: AddTemplateState, reduce: AddTemplateReduce): AddTemplateState {
    return when (reduce) {
      is AddTemplateReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is AddTemplateReduce.Initialize -> reduce.state
      is AddTemplateReduce.UpdateFoodStatus -> when (reduce.food) {
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
      is AddTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(AddTemplateSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize(templateName: String) {
    updateState(AddTemplateReduce.UpdateLoading(true))

    val foodList = getFoodListUseCase.invoke()
    updateState(
      AddTemplateReduce.Initialize(
        AddTemplateState(
          loading = false,
          name = templateName,
          foodMap = foodList.associate { FoodUIModel(it) to FoodStatus.DEFAULT }
        )
      )
    )
  }

  private suspend fun addTemplate() {
    updateState(AddTemplateReduce.UpdateLoading(true))

    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    addTemplateUseCase.invoke(
      Template(
        userId = userId,
        name = currentState.name,
        like = currentState.likeList.map { it.name },
        dislike = currentState.dislikeList.map { it.name },
      )
    )
    sendSideEffect(AddTemplateSideEffect.ShowSnackBar(UiText.DynamicString("템플릿이 저장되었습니다.")))
    sendSideEffect(AddTemplateSideEffect.PopBackStack)
  }
}