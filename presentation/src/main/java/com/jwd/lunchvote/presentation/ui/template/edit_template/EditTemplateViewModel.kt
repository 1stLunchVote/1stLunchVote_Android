package com.jwd.lunchvote.presentation.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.TemplateUIModel
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
      is EditTemplateEvent.OnClickFoodItem -> updateState(EditTemplateReduce.UpdateFoodStatus(event.foodItem))
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
      is EditTemplateReduce.UpdateFoodItemList -> state.copy(foodItemList = reduce.foodItemList)
      is EditTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is EditTemplateReduce.UpdateFoodStatus -> state.copy(
        foodItemList = state.foodItemList.map { if (it == reduce.foodItem) it.nextStatus() else it }
      )
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EditTemplateSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val templateIdKey = LunchVoteNavRoute.EditTemplate.arguments.first().name
    val templateId = checkNotNull(savedStateHandle.get<String>(templateIdKey))
    val template = templateRepository.getTemplateById(templateId).asUI()

    val foodItemList = foodRepository.getAllFood().map { food ->
      FoodItem(
        food = food.asUI(),
        status = when(food.id) {
          in template.likedFoodIds -> FoodItem.Status.LIKE
          in template.dislikedFoodIds -> FoodItem.Status.DISLIKE
          else -> FoodItem.Status.DEFAULT
        }
      )
    }

    updateState(EditTemplateReduce.UpdateTemplate(template))
    updateState(EditTemplateReduce.UpdateFoodItemList(foodItemList))
  }

  private suspend fun save() {
    sendSideEffect(EditTemplateSideEffect.CloseDialog)

    val likedFoodsId = currentState.foodItemList.filter { it.status == FoodItem.Status.LIKE }.map { it.food.id }
    val dislikedFoodsId = currentState.foodItemList.filter { it.status == FoodItem.Status.DISLIKE }.map { it.food.id }
    val updatedTemplate = TemplateUIModel(
      id = currentState.template.id,
      userId = currentState.template.userId,
      name = currentState.template.name,
      likedFoodIds = likedFoodsId,
      dislikedFoodIds = dislikedFoodsId
    )
    templateRepository.updateTemplate(updatedTemplate.asDomain())

    sendSideEffect(EditTemplateSideEffect.ShowSnackbar(UiText.StringResource(R.string.edit_template_save_snackbar)))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }

  private suspend fun delete() {
    sendSideEffect(EditTemplateSideEffect.CloseDialog)

    templateRepository.deleteTemplateById(currentState.template.id)

    sendSideEffect(EditTemplateSideEffect.ShowSnackbar(UiText.StringResource(R.string.edit_template_delete_snackbar)))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }
}