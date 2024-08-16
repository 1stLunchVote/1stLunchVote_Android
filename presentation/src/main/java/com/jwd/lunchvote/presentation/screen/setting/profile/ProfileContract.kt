package com.jwd.lunchvote.presentation.screen.setting.profile

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class ProfileContract {
  @Parcelize
  data class ProfileState(
    val user: UserUIModel = UserUIModel(),
    val profileImageUri: Uri = "".toUri(),
    val name: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ProfileEvent : ViewModelContract.Event {
    data object ScreenInitialize : ProfileEvent

    data object OnClickBackButton : ProfileEvent
    data object OnClickEditProfileImageButton : ProfileEvent
    data object OnClickEditNameButton : ProfileEvent
    data object OnClickDeleteUserButton : ProfileEvent

    // DialogEvent
    data class OnProfileImageChangeEditProfileImageDialog(val profileImageUri: Uri) : ProfileEvent
    data object OnImageLoadErrorEditProfileImageDialog : ProfileEvent
    data object OnClickCancelButtonEditProfileImageDialog : ProfileEvent
    data class OnClickSaveButtonEditProfileImageDialog(val context: Context) : ProfileEvent
    data class OnNameChangeEditNameDialog(val name: String) : ProfileEvent
    data object OnClickCancelButtonEditNameDialog : ProfileEvent
    data object OnClickSaveButtonEditNameDialog : ProfileEvent
    data object OnClickCancelButtonDeleteUserConfirmDialog : ProfileEvent
    data object OnClickConfirmButtonDeleteUserConfirmDialog : ProfileEvent
  }

  sealed interface ProfileReduce : ViewModelContract.Reduce {
    data class UpdateUser(val user: UserUIModel) : ProfileReduce
    data class UpdateProfileImage(val profileImageUri: Uri) : ProfileReduce
    data class UpdateName(val name: String) : ProfileReduce
  }

  sealed interface ProfileSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : ProfileSideEffect
    data object OpenEditProfileImageDialog : ProfileSideEffect
    data object OpenEditNameDialog : ProfileSideEffect
    data object OpenDeleteUserConfirmDialog : ProfileSideEffect
    data object CloseDialog : ProfileSideEffect
    data object NavigateToLogin : ProfileSideEffect
    data class ShowSnackbar(val message: UiText) : ProfileSideEffect
  }

  companion object {
    const val EDIT_PROFILE_IMAGE_DIALOG = "edit_profile_image_dialog"
    const val EDIT_NAME_DIALOG = "edit_name_dialog"
    const val DELETE_USER_CONFIRM_DIALOG = "delete_user_confirm_dialog"
  }
}