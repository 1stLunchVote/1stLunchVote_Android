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
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.domain.repository.StorageRepository
import com.jwd.lunchvote.domain.repository.UserRepository
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
import kr.co.inbody.config.error.UserError
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val storageRepository: StorageRepository,
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
      is ProfileEvent.ScreenInitialize -> launch { initialize() }

      is ProfileEvent.OnClickBackButton -> sendSideEffect(ProfileSideEffect.PopBackStack)
      is ProfileEvent.OnClickEditProfileImageButton -> sendSideEffect(ProfileSideEffect.OpenEditProfileImageDialog)
      is ProfileEvent.OnClickEditNameButton -> sendSideEffect(ProfileSideEffect.OpenEditNameDialog)
      is ProfileEvent.OnClickDeleteUserButton -> sendSideEffect(ProfileSideEffect.OpenDeleteUserConfirmDialog)

      // DialogEvent
      is ProfileEvent.OnProfileImageChangeEditProfileImageDialog -> updateState(ProfileReduce.UpdateProfileImage(event.profileImageUri))
      is ProfileEvent.OnImageLoadErrorEditProfileImageDialog -> sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.profile_edit_profile_image_dialog_image_load_error)))
      is ProfileEvent.OnClickCancelButtonEditProfileImageDialog -> sendSideEffect(ProfileSideEffect.CloseDialog)
      is ProfileEvent.OnClickSaveButtonEditProfileImageDialog -> launch { saveProfileImage(event.context) }
      is ProfileEvent.OnNameChangeEditNameDialog -> updateState(ProfileReduce.UpdateName(event.name))
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
    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val currentUser = Firebase.auth.currentUser ?: throw UserError.NoUser
    val user = userRepository.getUserById(currentUser.uid).asUI()

    updateState(ProfileReduce.UpdateUser(user))
    updateState(ProfileReduce.UpdateProfileImage(user.profileImage.toUri()))
    updateState(ProfileReduce.UpdateName(user.name))
  }

  private suspend fun saveProfileImage(context: Context) {
    sendSideEffect(ProfileSideEffect.CloseDialog)

    val imageBitmap = ImageBitmapFactory().createBitmapFromUri(context, currentState.profileImageUri)
    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "images")
      .apply { if (!exists()) mkdirs() }
    val file = File(directory, "${UUID.randomUUID()}.jpg").apply {
      outputStream().use { outputStream ->
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
      }
    }

    val imageUrl = file.readBytes()
    val image = storageRepository.uploadProfileImage(currentState.user.id, imageUrl)
    val user = currentState.user.copy(profileImage = image)
    userRepository.updateUser(user.asDomain())
    
    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.profile_edit_profile_image_success_snackbar)))
    initialize()
  }

  private suspend fun saveName() {
    sendSideEffect(ProfileSideEffect.CloseDialog)

    val user = currentState.user.copy(name = currentState.name)

    userRepository.updateUser(user.asDomain())
    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.profile_edit_name_success_snackbar)))
    initialize()
  }

  private suspend fun deleteUser() {
    sendSideEffect(ProfileSideEffect.CloseDialog)

    val currentUser = Firebase.auth.currentUser ?: throw UserError.NoUser
    currentUser.delete().await()

    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.profile_delete_user_success_snackbar)))
    sendSideEffect(ProfileSideEffect.NavigateToLogin)
  }
}