package com.jwd.lunchvote.presentation.screen.setting.profile

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class ProfileContract {
  @Parcelize
  data class ProfileState(
    val user: UserUIModel = UserUIModel(),
    val profileImageDialogState: ProfileImageDialogState? = null,
    val nameDialogState: NameDialogState? = null,
    val deleteDialogState: DeleteDialogState? = null
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ProfileEvent : ViewModelContract.Event {
    data object ScreenInitialize : ProfileEvent

    data object OnClickBackButton : ProfileEvent
    data object OnClickEditProfileImageButton : ProfileEvent
    data object OnClickEditNameButton : ProfileEvent
    data object OnClickDeleteUserButton : ProfileEvent
  }

  sealed interface ProfileReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : ProfileReduce
    data class UpdateProfileImageDialogState(val profileImageDialogState: ProfileImageDialogState?) : ProfileReduce
    data class UpdateNameDialogState(val nameDialogState: NameDialogState?) : ProfileReduce
    data class UpdateDeleteDialogState(val deleteDialogState: DeleteDialogState?) : ProfileReduce
  }

  sealed interface ProfileSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : ProfileSideEffect
    data object NavigateToLogin : ProfileSideEffect
    data class ShowSnackbar(val message: UiText) : ProfileSideEffect
  }

  @Parcelize
  data class ProfileImageDialogState(
    val profileImageUri: Uri
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ProfileImageDialogEvent : ProfileEvent {
    data class OnProfileImageChange(val profileImageUri: Uri) : ProfileImageDialogEvent
    data object OnImageLoadError : ProfileImageDialogEvent
    data object OnClickCancelButton : ProfileImageDialogEvent
    data class OnClickSaveButton(val context: Context) : ProfileImageDialogEvent
  }

  sealed interface ProfileImageDialogReduce : ProfileReduce {
    data class UpdateProfileImage(val profileImageUri: Uri) : ProfileImageDialogReduce
  }

  @Parcelize
  data class NameDialogState(
    val name: String
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface NameDialogEvent : ProfileEvent {
    data class OnNameChange(val name: String) : NameDialogEvent
    data object OnClickCancelButton : NameDialogEvent
    data object OnClickSaveButton : NameDialogEvent
  }

  sealed interface NameDialogReduce : ProfileReduce {
    data class UpdateName(val name: String) : NameDialogReduce
  }

  @Parcelize
  data object DeleteDialogState : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface DeleteDialogEvent : ProfileEvent {
    data object OnClickCancelButton : DeleteDialogEvent
    data object OnClickDeleteButton : DeleteDialogEvent
  }

  companion object {
    const val EDIT_PROFILE_IMAGE_DIALOG = "edit_profile_image_dialog"
    const val EDIT_NAME_DIALOG = "edit_name_dialog"
    const val DELETE_USER_CONFIRM_DIALOG = "delete_user_confirm_dialog"
  }
}