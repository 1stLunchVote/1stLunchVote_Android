package com.jwd.lunchvote.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.usecase.template.DeleteTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.EditTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.GetFoodsUseCase
import com.jwd.lunchvote.domain.usecase.template.GetTemplateUseCase
import com.jwd.lunchvote.model.FoodStatus.DEFAULT
import com.jwd.lunchvote.model.FoodStatus.DISLIKE
import com.jwd.lunchvote.model.FoodStatus.LIKE
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.model.updateFoodMap
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateDialogState
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateDialogState.DeleteTemplateConfirm
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateDialogState.EditTemplateConfirm
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.OnClickBackButton
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.OnClickDeleteButton
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.OnClickDialogDismiss
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.OnClickFood
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.OnClickSaveButton
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.SetSearchKeyword
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateEvent.StartInitialize
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateReduce
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateReduce.Initialize
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateReduce.UpdateFoodStatus
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateReduce.UpdateLoading
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateReduce.UpdateSearchKeyword
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateSideEffect
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateSideEffect.PopBackStack
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.EditTemplateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val getFoodsUseCase: GetFoodsUseCase,
  private val getTemplateUseCase: GetTemplateUseCase,
  private val editTemplateUseCase: EditTemplateUseCase,
  private val deleteTemplateUseCase: DeleteTemplateUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<EditTemplateState, EditTemplateEvent, EditTemplateReduce, EditTemplateSideEffect, EditTemplateDialogState>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): EditTemplateState {
    return savedState as? EditTemplateState ?: EditTemplateState()
  }

  init {
    val templateId = checkNotNull(savedStateHandle.get<String>("templateId"))
    sendEvent(StartInitialize(templateId))
  }

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is StartInitialize -> viewModelScope.launch { initialize(event.templateId) }
      is OnClickBackButton -> sendSideEffect(PopBackStack())
      is SetSearchKeyword -> updateState(UpdateSearchKeyword(event.searchKeyword))
      is OnClickFood -> {
        Timber.w("💛 ===ktw=== ${currentState.likeList.map { it.name }} ${currentState.dislikeList.map { it.name }}")
        updateState(UpdateFoodStatus(event.food))
      }
      is OnClickSaveButton -> toggleDialog(
        EditTemplateConfirm(
          onClickConfirm = { viewModelScope.launch { save() } }
        )
      )
      is OnClickDeleteButton -> toggleDialog(
        DeleteTemplateConfirm (
          onClickConfirm = { viewModelScope.launch { delete() } }
        )
      )
      is OnClickDialogDismiss -> toggleDialog(null)
    }
  }

  override fun reduceState(state: EditTemplateState, reduce: EditTemplateReduce): EditTemplateState {
    return when (reduce) {
      is UpdateLoading -> state.copy(loading = reduce.loading)
      is Initialize -> reduce.state
      is UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is UpdateFoodStatus -> when (reduce.food) {
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

  private suspend fun initialize(templateId: String) {
    updateState(UpdateLoading(true))

    val foodList = getFoodsUseCase.invoke()
    val template = getTemplateUseCase.invoke(templateId)
    updateState(
      Initialize(
        EditTemplateState(
          template = TemplateUIModel(template),
          foodMap = foodList.associate {
            FoodUIModel(it) to when (it.name) {
              in template.like -> LIKE
              in template.dislike -> DISLIKE
              else -> DEFAULT
            }
          },
          likeList = foodList.filter { template.like.contains(it.name) }.map { FoodUIModel(it) },
          dislikeList = foodList.filter { template.dislike.contains(it.name) }.map { FoodUIModel(it) }
        )
      )
    )
  }

  private suspend fun save() {
    updateState(UpdateLoading(true))

    editTemplateUseCase.invoke(
      Template(
        id = currentState.template.id,
        userId = currentState.template.userId,
        name = currentState.template.name,
        like = currentState.likeList.map { it.name },
        dislike = currentState.dislikeList.map { it.name }
      )
    )
    sendSideEffect(PopBackStack("템플릿이 수정되었습니다."))
  }

  private suspend fun delete() {
    updateState(UpdateLoading(true))

    deleteTemplateUseCase.invoke(currentState.template.id)
    sendSideEffect(PopBackStack("템플릿이 삭제되었습니다."))
  }
}