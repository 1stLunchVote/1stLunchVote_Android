package com.jwd.lunchvote.presentation.screen.home

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import com.jwd.lunchvote.domain.usecase.CreateFood
import com.jwd.lunchvote.domain.usecase.ExitLounge
import com.jwd.lunchvote.domain.usecase.GetFoodTrend
import com.jwd.lunchvote.presentation.BuildConfig
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeReduce
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.screen.home.HomeContract.JoinDialogEvent
import com.jwd.lunchvote.presentation.screen.home.HomeContract.JoinDialogReduce
import com.jwd.lunchvote.presentation.screen.home.HomeContract.JoinDialogState
import com.jwd.lunchvote.presentation.screen.home.HomeContract.SecretDialogEvent
import com.jwd.lunchvote.presentation.screen.home.HomeContract.SecretDialogReduce
import com.jwd.lunchvote.presentation.screen.home.HomeContract.SecretDialogState
import com.jwd.lunchvote.presentation.util.ImageBitmapFactory
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.inbody.config.error.LoungeError
import kr.co.inbody.config.error.UserError
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val userStatusRepository: UserStatusRepository,
  private val getFoodTrend: GetFoodTrend,
  private val exitLounge: ExitLounge,
  private val createFood: CreateFood,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<HomeState, HomeEvent, HomeReduce, HomeSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): HomeState {
    return savedState as? HomeState ?: HomeState()
  }

  private val userId: String
    get() = Firebase.auth.currentUser?.uid ?: throw UserError.NoSession

  override fun handleEvents(event: HomeEvent) {
    when(event) {
      is HomeEvent.ScreenInitialize -> launch { initialize() }

      is HomeEvent.OnClickLoungeButton -> sendSideEffect(HomeSideEffect.NavigateToLounge(null))
      is HomeEvent.OnClickJoinLoungeButton -> updateState(HomeReduce.UpdateJoinDialogState(JoinDialogState()))
      is HomeEvent.OnClickTemplateButton -> sendSideEffect(HomeSideEffect.NavigateToTemplateList)
      is HomeEvent.OnClickFriendButton -> sendSideEffect(HomeSideEffect.NavigateToFriendList)
      is HomeEvent.OnClickSettingButton -> sendSideEffect(HomeSideEffect.NavigateToSetting)
      is HomeEvent.OnClickTipsButton -> sendSideEffect(HomeSideEffect.NavigateToTips)
      is HomeEvent.OnLongPressIcon -> updateState(HomeReduce.UpdateSecretDialogState(SecretDialogState()))

      is JoinDialogEvent -> handleJoinDialogEvents(event)
      is SecretDialogEvent -> handleSecretDialogEvents(event)
    }
  }

  private fun handleJoinDialogEvents(event: JoinDialogEvent) {
    when(event) {
      is JoinDialogEvent.OnLoungeIdChange -> updateState(JoinDialogReduce.UpdateLoungeId(event.loungeId))
      is JoinDialogEvent.OnClickCancelButton -> updateState(HomeReduce.UpdateJoinDialogState(null))
      is JoinDialogEvent.OnClickJoinButton -> launch { checkLoungeExist() }
    }
  }

  private fun handleSecretDialogEvents(event: SecretDialogEvent) {
    when(event) {
      is SecretDialogEvent.OnFoodNameChange -> updateState(SecretDialogReduce.UpdateFoodName(event.foodName))
      is SecretDialogEvent.OnFoodImageChange -> updateState(SecretDialogReduce.UpdateFoodImageUri(event.foodImageUri))
      is SecretDialogEvent.OnImageLoadError -> sendSideEffect(HomeSideEffect.ShowSnackbar(UiText.StringResource(R.string.p_profile_image_dialog_image_load_error)))
      is SecretDialogEvent.OnClickCancelButton -> updateState(HomeReduce.UpdateSecretDialogState(null))
      is SecretDialogEvent.OnClickUploadButton -> launch { uploadFood(event.context) }
    }
  }

  override fun reduceState(state: HomeState, reduce: HomeReduce): HomeState {
    return when(reduce) {
      is HomeReduce.UpdateFoodTrend -> state.copy(foodTrend = reduce.foodTrend)
      is HomeReduce.UpdateFoodTrendRatio -> state.copy(foodTrendRatio = reduce.foodTrendRatio)
      is HomeReduce.UpdateJoinDialogState -> state.copy(joinDialogState = reduce.joinDialogState)
      is HomeReduce.UpdateSecretDialogState -> state.copy(secretDialogState = reduce.secretDialogState)

      is JoinDialogReduce -> state.copy(joinDialogState = reduceJoinDialogState(state.joinDialogState, reduce))
      is SecretDialogReduce -> state.copy(secretDialogState = reduceSecretDialogState(state.secretDialogState, reduce))
    }
  }

  private fun reduceJoinDialogState(state: JoinDialogState?, reduce: JoinDialogReduce): JoinDialogState? {
    return when(reduce) {
      is JoinDialogReduce.UpdateLoungeId -> state?.copy(loungeId = reduce.loungeId)
    }
  }

  private fun reduceSecretDialogState(state: SecretDialogState?, reduce: SecretDialogReduce): SecretDialogState? {
    return when(reduce) {
      is SecretDialogReduce.UpdateFoodName -> state?.copy(foodName = reduce.foodName)
      is SecretDialogReduce.UpdateFoodImageUri -> state?.copy(foodImageUri = reduce.foodImageUri)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(HomeSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val userStatus = userStatusRepository.getUserStatus(userId)
    if (userStatus?.loungeId != null) exitLounge(userId)

    val (foodTrend, foodTrendRatio) = getFoodTrend()

    updateState(HomeReduce.UpdateFoodTrend(foodTrend?.asUI()))
    updateState(HomeReduce.UpdateFoodTrendRatio(foodTrendRatio))
  }

  private suspend fun checkLoungeExist() {
    val dialogState = currentState.joinDialogState ?: return
    updateState(HomeReduce.UpdateJoinDialogState(null))

    val loungeId = dialogState.loungeId
    val isAvailable = loungeRepository.checkLoungeExistById(loungeId)

    if (isAvailable) sendSideEffect(HomeSideEffect.NavigateToLounge(loungeId))
    else throw LoungeError.NoLounge
  }

  private suspend fun uploadFood(context: Context) {
    val dialogState = currentState.secretDialogState ?: return
    updateState(HomeReduce.UpdateSecretDialogState(null))

    // Only for Debug
    if (BuildConfig.DEBUG) {
      val food = FoodUIModel(
        id = UUID.randomUUID().toString(),
        name = dialogState.foodName
      )

      val imageByteArray = ImageBitmapFactory.createByteArrayFromUri(context, dialogState.foodImageUri, dialogState.foodName)
      createFood(food.asDomain(), imageByteArray)
    }
  }
}