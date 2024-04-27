package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.DeleteTemplateUseCase
import com.jwd.lunchvote.domain.usecase.EditTemplateUseCase
import com.jwd.lunchvote.domain.usecase.GetFoodListUseCase
import com.jwd.lunchvote.domain.usecase.GetTemplateUseCase
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.enums.FoodStatus
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateReduce
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val getFoodListUseCase: GetFoodListUseCase,
  private val getTemplateUseCase: GetTemplateUseCase,
  private val editTemplateUseCase: EditTemplateUseCase,
  private val deleteTemplateUseCase: DeleteTemplateUseCase,
  private val savedStateHandle: SavedStateHandle
): BaseStateViewModel<EditTemplateState, EditTemplateEvent, EditTemplateReduce, EditTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): EditTemplateState {
    return savedState as? EditTemplateState ?: EditTemplateState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  init {
    launch {
      initialize()
    }
  }

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is EditTemplateEvent.OnClickBackButton -> sendSideEffect(EditTemplateSideEffect.PopBackStack)
      is EditTemplateEvent.SetSearchKeyword -> updateState(EditTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is EditTemplateEvent.OnClickFood -> updateState(EditTemplateReduce.UpdateFoodStatus(event.food))
      is EditTemplateEvent.OnClickSaveButton -> sendSideEffect(EditTemplateSideEffect.OpenConfirmDialog)
      is EditTemplateEvent.OnClickDeleteButton -> sendSideEffect(EditTemplateSideEffect.OpenDeleteDialog)

      // DialogEvent
      is EditTemplateEvent.OnClickCancelButtonConfirmDialog -> setDialogState("")
      is EditTemplateEvent.OnClickConfirmButtonConfirmDialog -> launch { save() }
      is EditTemplateEvent.OnClickCancelButtonDeleteDialog -> setDialogState("")
      is EditTemplateEvent.OnClickDeleteButtonDeleteDialog -> launch { delete() }
    }
  }

  override fun reduceState(state: EditTemplateState, reduce: EditTemplateReduce): EditTemplateState {
    return when (reduce) {
      is EditTemplateReduce.UpdateTemplate -> state.copy(template = reduce.template)
      is EditTemplateReduce.UpdateFoodMap -> state.copy(foodMap = reduce.foodMap)
      is EditTemplateReduce.UpdateLikeList -> state.copy(likeList = reduce.likeList)
      is EditTemplateReduce.UpdateDislikeList -> state.copy(dislikeList = reduce.dislikeList)
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

  private suspend fun initialize() {
    val templateId = checkNotNull(savedStateHandle.get<String>(LunchVoteNavRoute.EditTemplate.arguments.first().name))
    val template = getTemplateUseCase(templateId).asUI()

    val foodList = getFoodListUseCase().map { it.asUI() }
    val foodMap = foodList.associateWith {
      when (it.name) {
        in template.like -> FoodStatus.LIKE
        in template.dislike -> FoodStatus.DISLIKE
        else -> FoodStatus.DEFAULT
      }
    }
    val likeList = foodList.filter { template.like.contains(it.name) }
    val dislikeList = foodList.filter { template.dislike.contains(it.name) }
    
    updateState(EditTemplateReduce.UpdateTemplate(template))
    updateState(EditTemplateReduce.UpdateFoodMap(foodMap))
    updateState(EditTemplateReduce.UpdateLikeList(likeList))
    updateState(EditTemplateReduce.UpdateDislikeList(dislikeList))
  }

  private suspend fun save() {
    editTemplateUseCase(
      TemplateUIModel(
        id = currentState.template.id,
        userId = currentState.template.userId,
        name = currentState.template.name,
        like = currentState.likeList.map { it.name },
        dislike = currentState.dislikeList.map { it.name }
      ).asDomain()
    )

    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.DynamicString("템플릿이 수정되었습니다.")))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }

  private suspend fun delete() {
    deleteTemplateUseCase(currentState.template.id)

    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.DynamicString("템플릿이 삭제되었습니다.")))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }
}