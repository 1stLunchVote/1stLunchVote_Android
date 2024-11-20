package com.jwd.lunchvote.presentation.screen.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.DeleteDialogEvent
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.DeleteDialogState
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateReduce
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.EditTemplateState
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.SaveDialogEvent
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateContract.SaveDialogState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.RouteError
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val foodRepository: FoodRepository,
  private val templateRepository: TemplateRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<EditTemplateState, EditTemplateEvent, EditTemplateReduce, EditTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): EditTemplateState {
    return savedState as? EditTemplateState ?: EditTemplateState()
  }

  private val templateId: String =
    savedStateHandle[LunchVoteNavRoute.EditTemplate.arguments.first().name] ?: throw RouteError.NoArguments

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is EditTemplateEvent.ScreenInitialize -> launch { initialize() }

      is EditTemplateEvent.OnClickBackButton -> sendSideEffect(EditTemplateSideEffect.PopBackStack)
      is EditTemplateEvent.OnSearchKeywordChange -> updateState(EditTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is EditTemplateEvent.OnClickFoodItem -> updateState(EditTemplateReduce.UpdateFoodStatus(event.foodItem))
      is EditTemplateEvent.OnClickSaveButton -> updateState(EditTemplateReduce.UpdateSaveDialogState(SaveDialogState))
      is EditTemplateEvent.OnClickDeleteButton -> updateState(EditTemplateReduce.UpdateDeleteDialogState(DeleteDialogState))

      is SaveDialogEvent -> handleSaveDialogEvents(event)
      is DeleteDialogEvent -> handleDeleteDialogEvents(event)
    }
  }

  private fun handleSaveDialogEvents(event: SaveDialogEvent) {
    when(event) {
      is SaveDialogEvent.OnClickCancelButton -> updateState(EditTemplateReduce.UpdateSaveDialogState(null))
      is SaveDialogEvent.OnClickSaveButton -> launch { save() }
    }
  }

  private fun handleDeleteDialogEvents(event: DeleteDialogEvent) {
    when(event) {
      is DeleteDialogEvent.OnClickCancelButton -> updateState(EditTemplateReduce.UpdateDeleteDialogState(null))
      is DeleteDialogEvent.OnClickDeleteButton -> launch { delete() }
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
      is EditTemplateReduce.UpdateSaveDialogState -> state.copy(saveDialogState = reduce.saveDialogState)
      is EditTemplateReduce.UpdateDeleteDialogState -> state.copy(deleteDialogState = reduce.deleteDialogState)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(EditTemplateSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
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
    currentState.saveDialogState ?: return
    updateState(EditTemplateReduce.UpdateSaveDialogState(null))

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
    currentState.deleteDialogState ?: return
    updateState(EditTemplateReduce.UpdateSaveDialogState(null))

    templateRepository.deleteTemplateById(currentState.template.id)

    sendSideEffect(EditTemplateSideEffect.ShowSnackbar(UiText.StringResource(R.string.edit_template_delete_snackbar)))
    sendSideEffect(EditTemplateSideEffect.PopBackStack)
  }
}