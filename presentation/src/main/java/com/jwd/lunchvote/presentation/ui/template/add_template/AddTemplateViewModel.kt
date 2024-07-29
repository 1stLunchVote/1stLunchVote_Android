package com.jwd.lunchvote.presentation.ui.template.add_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.FoodRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateReduce
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.RouteError
import kr.co.inbody.config.error.UserError
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
  private val foodRepository: FoodRepository,
  private val templateRepository: TemplateRepository,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<AddTemplateState, AddTemplateEvent, AddTemplateReduce, AddTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): AddTemplateState {
    return savedState as? AddTemplateState ?: AddTemplateState()
  }

  private val name: String =
    savedStateHandle[LunchVoteNavRoute.AddTemplate.arguments.first().name] ?: throw RouteError.NoArguments

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: AddTemplateEvent) {
    when(event) {
      is AddTemplateEvent.ScreenInitialize -> launch { initialize() }

      is AddTemplateEvent.OnClickBackButton -> sendSideEffect(AddTemplateSideEffect.PopBackStack)
      is AddTemplateEvent.OnClickFoodItem -> updateState(AddTemplateReduce.UpdateFoodStatus(event.foodItem))
      is AddTemplateEvent.OnSearchKeywordChange -> updateState(AddTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is AddTemplateEvent.OnClickAddButton -> launch { addTemplate() }
    }
  }

  override fun reduceState(state: AddTemplateState, reduce: AddTemplateReduce): AddTemplateState {
    return when (reduce) {
      is AddTemplateReduce.UpdateName -> state.copy(name = reduce.name)
      is AddTemplateReduce.UpdateFoodItemList -> state.copy(foodItemList = reduce.foodItemList)
      is AddTemplateReduce.UpdateFoodStatus -> state.copy(
        foodItemList = state.foodItemList.map { if (it == reduce.foodItem) it.nextStatus() else it }
      )
      is AddTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(AddTemplateSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    updateState(AddTemplateReduce.UpdateName(name))

    val foodItemList = foodRepository.getAllFood().map { food ->
      FoodItem(food = food.asUI())
    }

    updateState(AddTemplateReduce.UpdateFoodItemList(foodItemList))
  }

  private suspend fun addTemplate() {
    val likedFoodIds = currentState.foodItemList.filter { it.status == FoodItem.Status.LIKE }.map { it.food.id }
    val dislikedFoodIds = currentState.foodItemList.filter { it.status == FoodItem.Status.DISLIKE }.map { it.food.id }
    val template = TemplateUIModel(
      userId = userId,
      name = currentState.name,
      likedFoodIds = likedFoodIds,
      dislikedFoodIds = dislikedFoodIds,
    )

    templateRepository.addTemplate(template.asDomain())

    sendSideEffect(AddTemplateSideEffect.ShowSnackbar(UiText.StringResource(R.string.add_template_add_snackbar)))
    sendSideEffect(AddTemplateSideEffect.PopBackStack)
  }
}