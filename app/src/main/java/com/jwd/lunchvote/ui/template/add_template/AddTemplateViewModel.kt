package com.jwd.lunchvote.ui.template.add_template

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel
import com.jwd.lunchvote.model.TemplateUIModel
import com.jwd.lunchvote.ui.template.add_template.AddTemplateContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): BaseStateViewModel<AddTemplateState, AddTemplateEvent, AddTemplateReduce, AddTemplateSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): AddTemplateState {
    return savedState as? AddTemplateState ?: AddTemplateState()
  }

  init {
    val templateName = checkNotNull(savedStateHandle.get<String>("templateName"))
    sendEvent(AddTemplateEvent.StartInitialize(templateName))
  }

  override fun handleEvents(event: AddTemplateEvent) {
    when(event) {
      is AddTemplateEvent.StartInitialize -> {
        updateState(AddTemplateReduce.UpdateLoading(true))
        CoroutineScope(ioDispatcher).launch {
          initialize(event.templateName)
        }
      }
      is AddTemplateEvent.OnClickBackButton -> sendSideEffect(AddTemplateSideEffect.PopBackStack())
      is AddTemplateEvent.OnClickFood -> updateState(AddTemplateReduce.UpdateFoodStatus(event.food))
      is AddTemplateEvent.SetSearchKeyword -> updateState(AddTemplateReduce.UpdateSearchKeyword(event.searchKeyword))
      is AddTemplateEvent.OnClickAddButton -> {
        updateState(AddTemplateReduce.UpdateLoading(true))
        CoroutineScope(ioDispatcher).launch {
          addTemplate()
        }
      }
    }
  }

  override fun reduceState(state: AddTemplateState, reduce: AddTemplateReduce): AddTemplateState {
    return when (reduce) {
      is AddTemplateReduce.UpdateLoading -> state.copy(loading = reduce.loading)
      is AddTemplateReduce.Initialize -> reduce.state
      is AddTemplateReduce.UpdateFoodStatus -> {
        when(reduce.food.status) {
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
      is AddTemplateReduce.UpdateSearchKeyword -> state.copy(searchKeyword = reduce.searchKeyword)
    }
  }

  private suspend fun initialize(templateName: String) {


    updateState(AddTemplateReduce.Initialize(
      AddTemplateState(
        loading = false,
        template = TemplateUIModel("", templateName, emptyList(), emptyList()),
        foodList = List(20) {
          FoodUIModel(
            foodId = it.toLong(),
            imageUrl = "",
            name = "음식명",
            status = FoodStatus.DEFAULT
          )
        }
      )
    ))
  }

  private suspend fun addTemplate() {

  }
}