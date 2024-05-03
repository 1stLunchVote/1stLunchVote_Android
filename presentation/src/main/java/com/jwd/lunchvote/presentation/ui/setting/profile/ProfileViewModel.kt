package com.jwd.lunchvote.presentation.ui.setting.profile

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.GetUserByIdUseCase
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileEvent
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileReduce
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileSideEffect
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileState
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val getUserByIdUseCase: GetUserByIdUseCase,
  savedStateHandle: SavedStateHandle
): BaseStateViewModel<ProfileState, ProfileEvent, ProfileReduce, ProfileSideEffect>(savedStateHandle) {
  override fun createInitialState(savedState: Parcelable?): ProfileState {
    return savedState as? ProfileState ?: ProfileState()
  }

  private val _dialogState = MutableStateFlow("")
  val dialogState: StateFlow<String> = _dialogState.asStateFlow()
  fun setDialogState(dialogState: String) {
    viewModelScope.launch {
      _dialogState.emit(dialogState)
    }
  }

  override fun handleEvents(event: ProfileEvent) {
    when(event) {
      is ProfileEvent.OnScreenLoaded -> launch { initialize() }
      is ProfileEvent.OnClickBackButton -> sendSideEffect(ProfileSideEffect.PopBackStack)
      is ProfileEvent.OnClickEditProfileImageButton -> sendSideEffect(ProfileSideEffect.OpenEditProfileImageDialog)
      is ProfileEvent.OnClickEditNameButton -> sendSideEffect(ProfileSideEffect.OpenEditNameDialog)
      is ProfileEvent.OnClickDeleteUserButton -> sendSideEffect(ProfileSideEffect.OpenDeleteUserConfirmDialog)

      // DialogEvent
      is ProfileEvent.OnProfileImageChangedEditProfileImageDialog -> updateState(ProfileReduce.UpdateProfileImage(event.profileImageUrl))
      is ProfileEvent.OnClickCancelButtonEditProfileImageDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickSaveButtonEditProfileImageDialog -> {}
      is ProfileEvent.OnNameChangedEditNameDialog -> updateState(ProfileReduce.UpdateName(event.name))
      is ProfileEvent.OnClickCancelButtonEditNameDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickSaveButtonEditNameDialog -> {}
      is ProfileEvent.OnClickCancelButtonDeleteUserConfirmDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickConfirmButtonDeleteUserConfirmDialog -> launch { deleteUser() }
    }
  }

  override fun reduceState(state: ProfileState, reduce: ProfileReduce): ProfileState {
    return when (reduce) {
      is ProfileReduce.UpdateUser -> state.copy(user = reduce.user)
      is ProfileReduce.UpdateProfileImage -> state.copy(profileImageUrl = reduce.profileImageUrl)
      is ProfileReduce.UpdateName -> state.copy(name = reduce.name)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(ProfileSideEffect.ShowSnackBar(UiText.DynamicString(error.message ?: UnknownError.UNKNOWN)))
    when (error) {
      is LoginError.NoUser -> {
        Firebase.auth.signOut()
        sendSideEffect(ProfileSideEffect.NavigateToLogin)
      }
    }
  }

  private suspend fun initialize() {
    val currentUser = Firebase.auth.currentUser ?: throw LoginError.NoUser
    val user = getUserByIdUseCase(currentUser.uid).asUI()

    updateState(ProfileReduce.UpdateUser(user))
  }

  private suspend fun deleteUser() {
    val currentUser = Firebase.auth.currentUser ?: throw LoginError.NoUser
    currentUser.delete().await()

    sendSideEffect(ProfileSideEffect.ShowSnackBar(UiText.DynamicString("정상적으로 탈퇴되었습니다.")))
    sendSideEffect(ProfileSideEffect.NavigateToLogin)
  }
}