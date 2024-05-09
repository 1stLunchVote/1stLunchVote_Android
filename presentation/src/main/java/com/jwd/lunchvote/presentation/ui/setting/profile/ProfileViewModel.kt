package com.jwd.lunchvote.presentation.ui.setting.profile

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Parcelable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.core.common.error.UnknownError
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.usecase.GetUserByIdUseCase
import com.jwd.lunchvote.domain.usecase.UpdateUserUseCase
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileEvent
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileReduce
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileSideEffect
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileState
import com.jwd.lunchvote.presentation.util.ImageBitmapFactory
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val getUserByIdUseCase: GetUserByIdUseCase,
  private val updateUserUseCase: UpdateUserUseCase,
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
      is ProfileEvent.OnProfileImageChangedEditProfileImageDialog -> updateState(ProfileReduce.UpdateProfileImage(event.profileImageUri))
      is ProfileEvent.OnImageLoadErrorEditProfileImageDialog -> sendSideEffect(ProfileSideEffect.ShowSnackBar(UiText.StringResource(R.string.profile_edit_profile_image_dialog_image_load_error)))
      is ProfileEvent.OnClickCancelButtonEditProfileImageDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickSaveButtonEditProfileImageDialog -> launch { saveProfileImage(event.context) }
      is ProfileEvent.OnNameChangedEditNameDialog -> updateState(ProfileReduce.UpdateName(event.name))
      is ProfileEvent.OnClickCancelButtonEditNameDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickSaveButtonEditNameDialog -> launch { saveName() }
      is ProfileEvent.OnClickCancelButtonDeleteUserConfirmDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickConfirmButtonDeleteUserConfirmDialog -> launch { deleteUser() }
    }
  }

  override fun reduceState(state: ProfileState, reduce: ProfileReduce): ProfileState {
    return when (reduce) {
      is ProfileReduce.UpdateUser -> state.copy(user = reduce.user)
      is ProfileReduce.UpdateProfileImage -> state.copy(profileImageUri = reduce.profileImageUri)
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
    updateState(ProfileReduce.UpdateProfileImage(user.profileImageUrl.toUri()))
    updateState(ProfileReduce.UpdateName(user.name))
  }

  private suspend fun saveProfileImage(context: Context) {
    val imageBitmap = ImageBitmapFactory().createBitmapFromUri(context, currentState.profileImageUri)
    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "images")
      .apply { if (!exists()) mkdirs() }
    val file = File(directory, "${UUID.randomUUID()}.jpg").apply {
      outputStream().use { outputStream ->
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
      }
    }

    val user = currentState.user.copy(profileImageUrl = file.absolutePath)
    updateUserUseCase(user.asDomain())
    
    sendSideEffect(ProfileSideEffect.ShowSnackBar(UiText.StringResource(R.string.profile_edit_profile_image_success_snackbar)))
    sendSideEffect(ProfileSideEffect.CloseDialog)
    initialize()
  }

  private suspend fun saveName() {
    val user = currentState.user.copy(name = currentState.name)

    updateUserUseCase(user.asDomain())
    sendSideEffect(ProfileSideEffect.ShowSnackBar(UiText.StringResource(R.string.profile_edit_name_success_snackbar)))
    sendSideEffect(ProfileSideEffect.CloseDialog)
    initialize()
  }

  private suspend fun deleteUser() {
    val currentUser = Firebase.auth.currentUser ?: throw LoginError.NoUser
    currentUser.delete().await()

    sendSideEffect(ProfileSideEffect.ShowSnackBar(UiText.StringResource(R.string.profile_delete_user_success_snackbar)))
    sendSideEffect(ProfileSideEffect.NavigateToLogin)
  }
}