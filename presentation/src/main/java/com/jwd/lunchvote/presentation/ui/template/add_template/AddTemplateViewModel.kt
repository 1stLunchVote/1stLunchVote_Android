package com.jwd.lunchvote.presentation.ui.template.add_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.AddTemplateUseCase
import com.jwd.lunchvote.domain.usecase.GetFoodListUseCase
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.TemplateUIModel
import com.jwd.lunchvote.presentation.model.type.FoodStatus
import com.jwd.lunchvote.presentation.model.updateFoodMap
import com.jwd.lunchvote.presentation.navigation.LunchVoteNavRoute
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateEvent
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateReduce
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateSideEffect
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateContract.AddTemplateState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
  private val addTemplateUseCase: AddTemplateUseCase,
  private val getFoodListUseCase: GetFoodListUseCase,
  private val auth: FirebaseAuth,
  private val savedStateHandle: SavedStateHandle
): BaseStateViewModel<AddTemplateState, AddTemplateEvent, AddTemplateReduce, AddTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): AddTemplateState {
    return savedState as? AddTemplateState ?: AddTemplateState()
  }

  override fun handleEvents(event: AddTemplateEvent) {
    when(event) {
      is AddTemplateEvent.ScreenInitialize -> launch { initialize() }

      is AddTemplateEvent.OnClickBackButton -> sendSideEffect(AddTemplateSideEffect.PopBackStack)
      is AddTemplateEvent.OnClickFood -> updateState(AddTemplateReduce.UpdateFoodStatus(event.food))
      is AddTemplateEvent.OnSearchKeywordChange -> updateState(AddTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is AddTemplateEvent.OnClickAddButton -> launch { addTemplate() }
    }
  }

  override fun reduceState(state: AddTemplateState, reduce: AddTemplateReduce): AddTemplateState {
    return when (reduce) {
      is AddTemplateReduce.UpdateName -> state.copy(name = reduce.name)
      is AddTemplateReduce.UpdateFoodMap -> state.copy(foodMap = reduce.foodMap)
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
    when (error) {
      is LoginError.NoUser -> Firebase.auth.signOut()
    }
  }

  private suspend fun initialize() {
    val nameKey = LunchVoteNavRoute.AddTemplate.arguments.first().name
    val name = checkNotNull(savedStateHandle.get<String>(nameKey))
    updateState(AddTemplateReduce.UpdateName(name))

    val foodList = getFoodListUseCase()
    val foodMap = foodList.associate { it.asUI() to FoodStatus.DEFAULT }
    updateState(AddTemplateReduce.UpdateFoodMap(foodMap))
  }

  private suspend fun addTemplate() {
    val userId = auth.currentUser?.uid ?: throw LoginError.NoUser
    val template = TemplateUIModel(
      userId = userId,
      name = currentState.name,
      like = currentState.likeList.map { it.name },
      dislike = currentState.dislikeList.map { it.name },
    )

    addTemplateUseCase(template.asDomain())

    sendSideEffect(AddTemplateSideEffect.ShowSnackBar(UiText.StringResource(R.string.add_template_add_snackbar)))
    sendSideEffect(AddTemplateSideEffect.PopBackStack)
  }
}