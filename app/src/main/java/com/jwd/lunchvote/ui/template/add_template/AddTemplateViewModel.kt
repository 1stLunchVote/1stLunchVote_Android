package com.jwd.lunchvote.ui.template.add_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.model.enums.FoodStatus.DEFAULT
import com.jwd.lunchvote.model.enums.FoodStatus.DISLIKE
import com.jwd.lunchvote.model.enums.FoodStatus.LIKE
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.usecase.template.AddTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.GetFoodsUseCase
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateDialogState
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent.OnClickAddButton
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent.OnClickBackButton
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent.OnClickFood
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent.SetSearchKeyword
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateEvent.StartInitialize
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateReduce
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateReduce.Initialize
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateReduce.UpdateFoodStatus
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateReduce.UpdateLoading
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateReduce.UpdateSearchKeyword
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect.PopBackStack
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.AddTemplateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
  private val addTemplateUseCase: AddTemplateUseCase,
  private val getFoodsUseCase: GetFoodsUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<AddTemplateState, AddTemplateEvent, AddTemplateReduce, AddTemplateSideEffect, AddTemplateDialogState>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): AddTemplateState {
    return savedState as? AddTemplateState ?: AddTemplateState()
  }

  init {
    val templateName = checkNotNull(savedStateHandle.get<String>("templateName"))
    sendEvent(StartInitialize(templateName))
  }

  override fun handleEvents(event: AddTemplateEvent) {
    when(event) {
      is StartInitialize -> viewModelScope.launch { initialize(event.templateName) }
      is OnClickBackButton -> sendSideEffect(PopBackStack())
      is OnClickFood -> updateState(UpdateFoodStatus(event.food))
      is SetSearchKeyword -> updateState(UpdateSearchKeyword(event.searchKeyword))
      is OnClickAddButton -> viewModelScope.launch { addTemplate() }
    }
  }

  override fun reduceState(state: AddTemplateState, reduce: AddTemplateReduce): AddTemplateState {
    return when (reduce) {
      is UpdateLoading -> state.copy(loading = reduce.loading)
      is Initialize -> reduce.state
      is UpdateFoodStatus -> {
        when(reduce.food.status) {
          DEFAULT -> state.copy(
            likeList = state.likeList + reduce.food.copy(status = LIKE),
            foodList = state.foodList.map { if (it == reduce.food) it.copy(status = LIKE) else it }
          )
          LIKE -> state.copy(
            likeList = state.likeList.filter { it != reduce.food },
            dislikeList = state.dislikeList + reduce.food.copy(status = DISLIKE),
            foodList = state.foodList.map { if (it == reduce.food) it.copy(status = DISLIKE) else it }
          )
          DISLIKE -> state.copy(
            dislikeList = state.dislikeList.filter { it != reduce.food },
            foodList = state.foodList.map { if (it == reduce.food) it.copy(status = DEFAULT) else it }
          )
        }
      }
      is UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
    }
  }

  private suspend fun initialize(templateName: String) {
    updateState(UpdateLoading(true))

    val foodList = getFoodsUseCase.invoke()
    updateState(
      Initialize(
        AddTemplateState(
          loading = false,
          name = templateName,
          foodList = foodList.map { FoodUIModel(it) }
        )
      )
    )
  }

  private suspend fun addTemplate() {
    updateState(UpdateLoading(true))

    val userId = "PIRjtPnKcmJfNbSNIidD"   // TODO: 임시
    addTemplateUseCase.invoke(
      Template(
        userId = userId,
        name = currentState.name,
        like = currentState.likeList.map { it.name },
        dislike = currentState.dislikeList.map { it.name },
      )
    )
    sendSideEffect(PopBackStack("템플릿이 저장되었습니다."))
  }
}