package com.jwd.lunchvote.ui.template.edit_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.domain.usecase.template.DeleteTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.EditTemplateUseCase
import com.jwd.lunchvote.domain.usecase.template.GetFoodsUseCase
import com.jwd.lunchvote.domain.usecase.template.GetTemplateUseCase
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.template.edit_template.EditTemplateContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTemplateViewModel @Inject constructor(
  private val getFoodsUseCase: GetFoodsUseCase,
  private val getTemplateUseCase: GetTemplateUseCase,
  private val editTemplateUseCase: EditTemplateUseCase,
  private val deleteTemplateUseCase: DeleteTemplateUseCase,
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): BaseStateViewModel<EditTemplateState, EditTemplateEvent, EditTemplateReduce, EditTemplateSideEffect, EditTemplateDialogState>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): EditTemplateState {
    return savedState as? EditTemplateState ?: EditTemplateState()
  }

  init {
    val templateId = checkNotNull(savedStateHandle.get<String>("templateId"))
    sendEvent(EditTemplateEvent.StartInitialize(templateId))
  }

  override fun handleEvents(event: EditTemplateEvent) {
    when(event) {
      is EditTemplateEvent.StartInitialize -> {
        CoroutineScope(ioDispatcher).launch {
          initialize(event.templateId)
        }
      }
      is EditTemplateEvent.OnClickBackButton -> sendSideEffect(EditTemplateSideEffect.PopBackStack())
      is EditTemplateEvent.SetSearchKeyword -> updateState(EditTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is EditTemplateEvent.OnClickFood -> updateState(EditTemplateReduce.UpdateFoodStatus(event.food))
      is EditTemplateEvent.OnClickSaveButton -> toggleDialog(
        EditTemplateDialogState.EditTemplateConfirm {
          CoroutineScope(ioDispatcher).launch {
            save()
          }
        }
      )
      is EditTemplateEvent.OnClickDeleteButton -> toggleDialog(
        EditTemplateDialogState.DeleteTemplateConfirm {
          CoroutineScope(ioDispatcher).launch {
            delete()
          }
        }
      )
      is EditTemplateEvent.OnClickDialogDismiss -> toggleDialog(null)
    }
  }

  override fun reduceState(state: EditTemplateState, reduce: EditTemplateReduce): EditTemplateState {
    return when (reduce) {
      is EditTemplateReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is EditTemplateReduce.Initialize -> reduce.state
      is EditTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
      is EditTemplateReduce.UpdateFoodStatus -> {
        when (reduce.food.status) {
          FoodStatus.DEFAULT -> state.copy(
            likeList = state.likeList + reduce.food.copy(status = FoodStatus.LIKE),
            foodList = state.foodList.map {
              if (it == reduce.food) it.copy(status = FoodStatus.LIKE) else it
            }
          )

          FoodStatus.LIKE -> state.copy(
            likeList = state.likeList.filter { it != reduce.food },
            dislikeList = state.dislikeList + reduce.food.copy(status = FoodStatus.DISLIKE),
            foodList = state.foodList.map {
              if (it == reduce.food) it.copy(status = FoodStatus.DISLIKE) else it
            }
          )

          FoodStatus.DISLIKE -> state.copy(
            dislikeList = state.dislikeList.filter { it != reduce.food },
            foodList = state.foodList.map {
              if (it == reduce.food) it.copy(status = FoodStatus.DEFAULT) else it
            }
          )
        }
      }
    }
  }

  private suspend fun initialize(templateId: String) {
    updateState(EditTemplateReduce.UpdateLoading(true))

    val foodList = getFoodsUseCase.invoke()
    val template = getTemplateUseCase.invoke(templateId)

    updateState(EditTemplateReduce.Initialize(
      EditTemplateState(
        template = TemplateUIModel(template),
        foodList = foodList.map {
          when (it.name) {
            in template.like -> FoodUIModel(it, FoodStatus.LIKE)
            in template.dislike -> FoodUIModel(it, FoodStatus.DISLIKE)
            else -> FoodUIModel(it)
          }
        },
        likeList = foodList.filter { template.like.contains(it.name) }.map { FoodUIModel(it, FoodStatus.LIKE) },
        dislikeList = foodList.filter { template.dislike.contains(it.name) }.map { FoodUIModel(it, FoodStatus.DISLIKE) }
      )
    ))
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
    sendSideEffect(EditTemplateSideEffect.PopBackStack("템플릿이 수정되었습니다."))
  }

  private suspend fun delete() {
    updateState(EditTemplateReduce.UpdateLoading(true))

    deleteTemplateUseCase.invoke(currentState.template.id)
    sendSideEffect(EditTemplateSideEffect.PopBackStack("템플릿이 삭제되었습니다."))
  }
}