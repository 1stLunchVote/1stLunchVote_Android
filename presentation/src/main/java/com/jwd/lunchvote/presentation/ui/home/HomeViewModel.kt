package com.jwd.lunchvote.presentation.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.usecase.CreateFood
import com.jwd.lunchvote.domain.usecase.GetFoodTrend
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeReduce
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.util.ImageBitmapFactory
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.inbody.config.error.LoungeError
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val getFoodTrend: GetFoodTrend,
  savedStateHandle: SavedStateHandle,

  // TODO: Temporary Secret UseCase
  private val createFood: CreateFood
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle){
  override fun createInitialState(savedState: Parcelable?): HomeState {
    return savedState as? HomeState ?: HomeState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: HomeEvent) {
    when(event) {
      is HomeEvent.ScreenInitialize -> launch { initialize() }

      is HomeEvent.OnClickLoungeButton -> sendSideEffect(HomeSideEffect.NavigateToLounge(currentState.loungeId))
      is HomeEvent.OnClickJoinLoungeButton -> sendSideEffect(HomeSideEffect.OpenJoinDialog)
      is HomeEvent.OnClickTemplateButton -> sendSideEffect(HomeSideEffect.NavigateToTemplateList)
      is HomeEvent.OnClickFriendButton -> sendSideEffect(HomeSideEffect.NavigateToFriendList)
      is HomeEvent.OnClickSettingButton -> sendSideEffect(HomeSideEffect.NavigateToSetting)
      is HomeEvent.OnClickTipsButton -> sendSideEffect(HomeSideEffect.NavigateToTips)

      // DialogEvent
      is HomeEvent.OnLoungeIdChange -> updateState(HomeReduce.UpdateLoungeId(event.loungeId))
      is HomeEvent.OnClickCancelButtonJoinDialog -> {
        updateState(HomeReduce.UpdateLoungeId(null))
        sendSideEffect(HomeSideEffect.CloseDialog)
      }
      is HomeEvent.OnClickConfirmButtonJoinDialog -> launch { checkLoungeExist() }

      // TODO: Temporary Secret Events
      is HomeEvent.OnClickSecretButton -> sendSideEffect(HomeSideEffect.OpenSecretDialog)
      is HomeEvent.OnFoodNameChangeOfSecretDialog -> updateState(HomeReduce.UpdateFoodName(event.foodName))
      is HomeEvent.OnFoodImageChangeOfSecretDialog -> updateState(HomeReduce.UpdateFoodImageUri(event.foodImageUri))
      is HomeEvent.OnImageLoadErrorOfSecretDialog -> sendSideEffect(HomeSideEffect.ShowSnackbar(UiText.StringResource(R.string.profile_edit_profile_image_dialog_image_load_error)))
      is HomeEvent.OnClickCancelButtonOfSecretDialog -> {
        sendSideEffect(HomeSideEffect.CloseDialog)
        updateState(HomeReduce.UpdateFoodName(null))
        updateState(HomeReduce.UpdateFoodImageUri(null))
      }
      is HomeEvent.OnClickUploadButtonOfSecretDialog -> launch { uploadFood(event.context) }
    }
  }

  override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
    return when(reduce) {
      is HomeReduce.UpdateFoodTrend -> state.copy(foodTrend = reduce.foodTrend)
      is HomeReduce.UpdateFoodTrendRatio -> state.copy(foodTrendRatio = reduce.foodTrendRatio)
      is HomeReduce.UpdateLoungeId -> state.copy(loungeId = reduce.loungeId)

      // TODO: Temporary Secret Reduces
      is HomeReduce.UpdateFoodName -> state.copy(foodName = reduce.foodName)
      is HomeReduce.UpdateFoodImageUri -> state.copy(foodImageUri = reduce.foodImageUri)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(HomeSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val (foodTrend, foodTrendRatio) = getFoodTrend()

    updateState(HomeReduce.UpdateFoodTrend(foodTrend?.asUI()))
    updateState(HomeReduce.UpdateFoodTrendRatio(foodTrendRatio))
  }

  private suspend fun checkLoungeExist() {
    val loungeId = currentState.loungeId ?: return
    updateState(HomeReduce.UpdateLoungeId(null))
    sendSideEffect(HomeSideEffect.CloseDialog)
    sendSideEffect(HomeSideEffect.ShowSnackbar(UiText.StringResource(R.string.home_joining_lounge_snackbar)))

    val isAvailable = loungeRepository.checkLoungeExistById(loungeId)

    if (isAvailable) sendSideEffect(HomeSideEffect.NavigateToLounge(loungeId))
    else throw LoungeError.NoLounge
  }

  // TODO: Temporary Secret Functions
  private suspend fun uploadFood(context: Context) {
    sendSideEffect(HomeSideEffect.CloseDialog)

    val imageBitmap = ImageBitmapFactory().createBitmapFromUri(context, currentState.foodImageUri ?: return)
    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "images")
      .apply { if (!exists()) mkdirs() }
    val file = File(directory, "${currentState.foodName}.jpg").apply {
      outputStream().use { outputStream ->
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
      }
    }

    val imageByteArray = file.readBytes()
    val food = FoodUIModel(
      id = UUID.randomUUID().toString(),
      name = currentState.foodName ?: return
    )

    createFood(food.asDomain(), imageByteArray)
    
    updateState(HomeReduce.UpdateFoodName(null))
    updateState(HomeReduce.UpdateFoodImageUri(null))
  }
}