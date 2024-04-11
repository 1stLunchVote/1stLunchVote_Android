package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.base.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.usecase.template.DeleteTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.EditTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.GetFoodListUseCase
import com.jwd.lunchvote.domain.usecase.template.GetTemplateUseCase
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateReduce
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val getFoodListUseCase: GetFoodListUseCase,
  private val getTemplateUseCase: GetTemplateUseCase,
  private val editTemplateUseCase: EditTemplateUseCase,
  private val deleteTemplateUseCase: DeleteTemplateUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<EditTemplateState, EditTemplateEvent, EditTemplateReduce, EditTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): EditTemplateState {
    return savedState as? EditTemplateState ?: EditTemplateState()
  }

  init {
    val templateId = checkNotNull(savedStateHandle.get<String>("templateId"))
    sendEvent(EditTemplateEvent.StartInitialize(templateId))
  }

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is EditTemplateEvent.StartInitialize -> viewModelScope.launch { initialize(event.templateId) }
      is EditTemplateEvent.OnClickBackButton -> sendSideEffect(EditTemplateSideEffect.PopBackStack)
      is EditTemplateEvent.SetSearchKeyword -> updateState(
        EditTemplateReduce.UpdateSearchKeyword(
          event.searchKeyword
        )
      )
      is EditTemplateEvent.OnClickFood -> updateState(EditTemplateReduce.UpdateFoodStatus(event.food))
      is EditTemplateEvent.OnClickSaveButton -> sendSideEffect(EditTemplateSideEffect.OpenConfirmDialog)
      is EditTemplateEvent.OnClickDeleteButton -> sendSideEffect(EditTemplateSideEffect.OpenDeleteDialog)
    }
  }

  override fun reduceState(state: EditTemplateState, reduce: EditTemplateReduce): EditTemplateState {
    return when (reduce) {
      is EditTemplateReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is EditTemplateReduce.Initialize -> reduce.state
      is EditTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is EditTemplateReduce.UpdateFoodStatus -> when (reduce.food) {
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
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize(templateId: String) {
    updateState(EditTemplateReduce.UpdateLoading(true))

    val foodList = getFoodListUseCase.invoke()
    val template = getTemplateUseCase.invoke(templateId)
    updateState(
      EditTemplateReduce.Initialize(
        EditTemplateState(
          template = TemplateUIModel(template),
          foodMap = foodList.associate {
            FoodUIModel(it) to when (it.name) {
              in template.like -> FoodStatus.LIKE
              in template.dislike -> FoodStatus.DISLIKE
              else -> FoodStatus.DEFAULT
            }
          },
          likeList = foodList.filter { template.like.contains(it.name) }.map { FoodUIModel(it) },
          dislikeList = foodList.filter { template.dislike.contains(it.name) }
            .map { FoodUIModel(it) }
        )
      )
    )
  }

  private suspend fun save() {
    updateState(EditTemplateReduce.UpdateLoading(true))

    editTemplateUseCase.invoke(
      Template(
        id = currentState.template.id,
        userId = currentState.template.userId,
        name = currentState.template.name,
        like = currentState.likeList.map { it.name },
        dislike = currentState.dislikeList.map { it.name }
      )
    )
    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.DynamicString("템플릿이 수정되었습니다.")))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }

  private suspend fun delete() {
    updateState(EditTemplateReduce.UpdateLoading(true))

    deleteTemplateUseCase.invoke(currentState.template.id)
    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.DynamicString("템플릿이 삭제되었습니다.")))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }
}