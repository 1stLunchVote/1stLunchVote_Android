package com.jwd.lunchvote.presentation.screen.setting.profile

import android.content.Context
import android.os.Parcelable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jwd.lunchvote.domain.repository.StorageRepository
import com.jwd.lunchvote.domain.repository.UserRepository
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.base.BaseStateViewModel
import com.jwd.lunchvote.presentation.mapper.asDomain
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.DeleteDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.DeleteDialogState
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.NameDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.NameDialogReduce
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.NameDialogState
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileImageDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileImageDialogReduce
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileImageDialogState
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileReduce
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileSideEffect
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileState
import com.jwd.lunchvote.presentation.util.ImageBitmapFactory
import com.jwd.lunchvote.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import kr.co.inbody.config.error.UserError
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

  override fun handleEvents(event: ProfileEvent) {
    when(event) {
      is ProfileEvent.ScreenInitialize -> launch { initialize() }

      is ProfileEvent.OnClickBackButton -> sendSideEffect(ProfileSideEffect.PopBackStack)
      is ProfileEvent.OnClickEditProfileImageButton -> updateState(ProfileReduce.UpdateProfileImageDialogState(ProfileImageDialogState(currentState.user.profileImage.toUri())))
      is ProfileEvent.OnClickEditNameButton -> updateState(ProfileReduce.UpdateNameDialogState(NameDialogState(currentState.user.name)))
      is ProfileEvent.OnClickDeleteUserButton -> updateState(ProfileReduce.UpdateDeleteDialogState(DeleteDialogState))

      is ProfileImageDialogEvent -> handleProfileImageDialogEvents(event)
      is NameDialogEvent -> handleNameDialogEvents(event)
      is DeleteDialogEvent -> handleDeleteDialogEvents(event)
    }
  }

  private fun handleProfileImageDialogEvents(event: ProfileImageDialogEvent) {
    when (event) {
      is ProfileImageDialogEvent.OnProfileImageChange -> updateState(ProfileImageDialogReduce.UpdateProfileImage(event.profileImageUri))
      is ProfileImageDialogEvent.OnImageLoadError -> sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.p_profile_image_dialog_image_load_error)))
      is ProfileImageDialogEvent.OnClickCancelButton -> updateState(ProfileReduce.UpdateProfileImageDialogState(null))
      is ProfileImageDialogEvent.OnClickSaveButton -> launch { saveProfileImage(event.context) }
    }
  }

  private fun handleNameDialogEvents(event: NameDialogEvent) {
    when (event) {
      is NameDialogEvent.OnNameChange -> updateState(NameDialogReduce.UpdateName(event.name))
      is NameDialogEvent.OnClickCancelButton -> updateState(ProfileReduce.UpdateNameDialogState(null))
      is NameDialogEvent.OnClickSaveButton -> launch { saveName() }
    }
  }

  private fun handleDeleteDialogEvents(event: DeleteDialogEvent) {
    when (event) {
      is DeleteDialogEvent.OnClickCancelButton -> updateState(ProfileReduce.UpdateDeleteDialogState(null))
      is DeleteDialogEvent.OnClickDeleteButton -> launch { deleteUser() }
    }
  }

  override fun reduceState(state: ProfileState, reduce: ProfileReduce): ProfileState {
    return when (reduce) {
      is ProfileReduce.UpdateUser -> state.copy(user = reduce.user)
      is ProfileReduce.UpdateProfileImageDialogState -> state.copy(profileImageDialogState = reduce.profileImageDialogState)
      is ProfileReduce.UpdateNameDialogState -> state.copy(nameDialogState = reduce.nameDialogState)
      is ProfileReduce.UpdateDeleteDialogState -> state.copy(deleteDialogState = reduce.deleteDialogState)

      is ProfileImageDialogReduce -> state.copy(profileImageDialogState = reduceProfileImageDialogState(state.profileImageDialogState, reduce))
      is NameDialogReduce -> state.copy(nameDialogState = reduceNameDialogState(state.nameDialogState, reduce))
    }
  }

  private fun reduceProfileImageDialogState(state: ProfileImageDialogState?, reduce: ProfileImageDialogReduce): ProfileImageDialogState? {
    return when (reduce) {
      is ProfileImageDialogReduce.UpdateProfileImage -> state?.copy(profileImageUri = reduce.profileImageUri)
    }
  }

  private fun reduceNameDialogState(state: NameDialogState?, reduce: NameDialogReduce): NameDialogState? {
    return when (reduce) {
      is NameDialogReduce.UpdateName -> state?.copy(name = reduce.name)
    }
  }

  override fun handleErrors(error: Throwable) {
    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.ErrorString(error)))
  }

  private suspend fun initialize() {
    val currentUser = Firebase.auth.currentUser ?: throw UserError.NoUser
    val user = userRepository.getUserById(currentUser.uid).asUI()

    updateState(ProfileReduce.UpdateUser(user))
  }

  private suspend fun saveProfileImage(context: Context) {
    val dialogState = currentState.profileImageDialogState ?: return
    updateState(ProfileReduce.UpdateProfileImageDialogState(null))

    val imageByteArray = ImageBitmapFactory.createByteArrayFromUri(context, dialogState.profileImageUri, UUID.randomUUID().toString())
    val image = storageRepository.uploadProfileImage(currentState.user.id, imageByteArray)
    val user = currentState.user.copy(profileImage = image)
    userRepository.updateUser(user.asDomain())
    
    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.p_profile_image_success_snackbar)))
    initialize()
  }

  private suspend fun saveName() {
    val dialogState = currentState.nameDialogState ?: return

    val nameExists = userRepository.checkNameExists(dialogState.name)
    if (nameExists) throw UserError.DuplicatedName

    updateState(ProfileReduce.UpdateNameDialogState(null))

    val user = currentState.user.copy(name = dialogState.name)

    userRepository.updateUser(user.asDomain())
    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.p_name_success_snackbar)))
    initialize()
  }

  private suspend fun deleteUser() {
    currentState.deleteDialogState ?: return
    updateState(ProfileReduce.UpdateDeleteDialogState(null))

    val currentUser = Firebase.auth.currentUser ?: throw UserError.NoUser
    currentUser.delete().await()

    sendSideEffect(ProfileSideEffect.ShowSnackbar(UiText.StringResource(R.string.p_delete_dialog_success_snackbar)))
    sendSideEffect(ProfileSideEffect.NavigateToLogin)
  }
}