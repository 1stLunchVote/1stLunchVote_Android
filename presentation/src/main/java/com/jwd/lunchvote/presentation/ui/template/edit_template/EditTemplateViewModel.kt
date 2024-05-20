package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.FoodStatus
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
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val foodRepository: FoodRepository,
  private val templateRepository: TemplateRepository,
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

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is EditTemplateEvent.ScreenInitialize -> launch { initialize() }

      is EditTemplateEvent.OnClickBackButton -> sendSideEffect(EditTemplateSideEffect.PopBackStack)
      is EditTemplateEvent.OnSearchKeywordChange -> updateState(EditTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is EditTemplateEvent.OnClickFood -> updateState(EditTemplateReduce.UpdateFoodStatus(event.food))
      is EditTemplateEvent.OnClickSaveButton -> sendSideEffect(EditTemplateSideEffect.OpenConfirmDialog)
      is EditTemplateEvent.OnClickDeleteButton -> sendSideEffect(EditTemplateSideEffect.OpenDeleteDialog)

      // DialogEvent
      is EditTemplateEvent.OnClickCancelButtonConfirmDialog -> sendSideEffect(EditTemplateSideEffect.CloseDialog)
      is EditTemplateEvent.OnClickConfirmButtonConfirmDialog -> launch { save() }
      is EditTemplateEvent.OnClickCancelButtonDeleteDialog -> sendSideEffect(EditTemplateSideEffect.CloseDialog)
      is EditTemplateEvent.OnClickDeleteButtonDeleteDialog -> launch { delete() }
    }
  }

  override fun reduceState(state: EditTemplateState, reduce: EditTemplateReduce): EditTemplateState {
    return when (reduce) {
      is EditTemplateReduce.UpdateTemplate -> state.copy(template = reduce.template)
      is EditTemplateReduce.UpdateFoodMap -> state.copy(foodMap = reduce.foodMap)
      is EditTemplateReduce.UpdateLikedFoods -> state.copy(likedFoods = reduce.likedFoods)
      is EditTemplateReduce.UpdateDislikedFoods -> state.copy(dislikedFoods = reduce.dislikedFoods)
      is EditTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is EditTemplateReduce.UpdateFoodStatus -> when (reduce.food) {
        in state.likedFoods -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          likedFoods = state.likedFoods.filter { it.id != reduce.food.id },
          dislikedFoods = state.dislikedFoods + reduce.food
        )
        in state.dislikedFoods -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          dislikedFoods = state.dislikedFoods.filter { it.id != reduce.food.id }
        )
        else -> state.copy(
          foodMap = state.foodMap.updateFoodMap(reduce.food),
          likedFoods = state.likedFoods + reduce.food
        )
      }
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
  }

  private suspend fun initialize() {
    val templateIdKey = LunchVoteNavRoute.EditTemplate.arguments.first().name
    val templateId = checkNotNull(savedStateHandle.get<String>(templateIdKey))
    val template = templateRepository.getTemplateById(templateId).asUI()

    val foodList = foodRepository.getAllFood().map { it.asUI() }
    val foodMap = foodList.associateWith {
      when (it.name) {
        in template.likedFoodIds -> FoodStatus.LIKE
        in template.dislikedFoodIds -> FoodStatus.DISLIKE
        else -> FoodStatus.DEFAULT
      }
    }
    val likeList = foodList.filter { template.likedFoodIds.contains(it.name) }
    val dislikeList = foodList.filter { template.dislikedFoodIds.contains(it.name) }
    
    updateState(EditTemplateReduce.UpdateTemplate(template))
    updateState(EditTemplateReduce.UpdateFoodMap(foodMap))
    updateState(EditTemplateReduce.UpdateLikedFoods(likeList))
    updateState(EditTemplateReduce.UpdateDislikedFoods(dislikeList))
  }

  private suspend fun save() {
    sendSideEffect(EditTemplateSideEffect.CloseDialog)

    templateRepository.updateTemplate(
      TemplateUIModel(
        id = currentState.template.id,
        userId = currentState.template.userId,
        name = currentState.template.name,
        likedFoodIds = currentState.likedFoods.map { it.name },
        dislikedFoodIds = currentState.dislikedFoods.map { it.name }
      ).asDomain()
    )

    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.StringResource(R.string.edit_template_save_snackbar)))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }

  private suspend fun delete() {
    sendSideEffect(EditTemplateSideEffect.CloseDialog)

    templateRepository.deleteTemplateById(currentState.template.id)

    sendSideEffect(EditTemplateSideEffect.ShowSnackBar(UiText.StringResource(R.string.edit_template_delete_snackbar)))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }
}