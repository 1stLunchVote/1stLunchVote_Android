package com.jwd.lunchvote.presentation.screen.setting.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileSideEffect
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.ImageBitmapFactory
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun ProfileRoute(
  popBackStack: () -> Unit,
  navigateToLogin: () -> Unit,
  viewModel: ProfileViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is ProfileSideEffect.PopBackStack -> popBackStack()
        is ProfileSideEffect.OpenEditProfileImageDialog -> viewModel.setDialogState(ProfileContract.EDIT_PROFILE_IMAGE_DIALOG)
        is ProfileSideEffect.OpenEditNameDialog -> viewModel.setDialogState(ProfileContract.EDIT_NAME_DIALOG)
        is ProfileSideEffect.OpenDeleteUserConfirmDialog -> viewModel.setDialogState(ProfileContract.DELETE_USER_CONFIRM_DIALOG)
        is ProfileSideEffect.CloseDialog -> viewModel.setDialogState("")
        is ProfileSideEffect.NavigateToLogin -> navigateToLogin()
        is ProfileSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  when (dialog) {
    ProfileContract.EDIT_PROFILE_IMAGE_DIALOG -> {
      EditProfileImageDialog(
        profileImageUri = state.profileImageUri,
        onDismissRequest = { viewModel.sendEvent(ProfileEvent.OnClickCancelButtonEditProfileImageDialog) },
        onProfileImageChange = { viewModel.sendEvent(ProfileEvent.OnProfileImageChangeEditProfileImageDialog(it)) },
        onImageError = { viewModel.sendEvent(ProfileEvent.OnImageLoadErrorEditProfileImageDialog) },
        onConfirmation = { viewModel.sendEvent(ProfileEvent.OnClickSaveButtonEditProfileImageDialog(context)) }
      )
    }
    ProfileContract.EDIT_NAME_DIALOG -> {
      EditNameDialog(
        initialName = state.user.name,
        name = state.name,
        onDismissRequest = { viewModel.sendEvent(ProfileEvent.OnClickCancelButtonEditNameDialog) },
        onNameChange = { viewModel.sendEvent(ProfileEvent.OnNameChangeEditNameDialog(it)) },
        onConfirmation = { viewModel.sendEvent(ProfileEvent.OnClickSaveButtonEditNameDialog) }
      )
    }
    ProfileContract.DELETE_USER_CONFIRM_DIALOG -> {
      DeleteUserConfirmDialog(
        onDismissRequest = { viewModel.sendEvent(ProfileEvent.OnClickCancelButtonDeleteUserConfirmDialog) },
        onConfirmation = { viewModel.sendEvent(ProfileEvent.OnClickConfirmButtonDeleteUserConfirmDialog) }
      )
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(ProfileEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else ProfileScreen(
    state = state,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun ProfileScreen(
  state: ProfileState,
  modifier: Modifier = Modifier,
  onEvent: (ProfileEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.profile_title),
        popBackStack = { onEvent(ProfileEvent.OnClickBackButton) }
      )
    },
    scrollable = false
  ) {
    ProfileTicket(
      profileImage = state.user.profileImage,
      name = state.user.name,
      email = state.user.email,
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterHorizontally)
    ) {
      TextButton(
        onClick = { onEvent(ProfileEvent.OnClickEditProfileImageButton) }
      ) {
        Text(
          text = stringResource(R.string.profile_edit_profile_image),
          textDecoration = TextDecoration.Underline
        )
      }
      TextButton(
        onClick = { onEvent(ProfileEvent.OnClickEditNameButton) }
      ) {
        Text(
          text = stringResource(R.string.profile_edit_name),
          textDecoration = TextDecoration.Underline
        )
      }
    }
    Gap(minHeight = 32.dp)
    TextButton(
      onClick = { onEvent(ProfileEvent.OnClickDeleteUserButton) }
    ) {
      Text(
        text = stringResource(R.string.profile_delete_user),
        color = MaterialTheme.colorScheme.error,
        textDecoration = TextDecoration.Underline
      )
    }
    Gap(height = 32.dp)
  }
}

@Composable
private fun ProfileTicket(
  profileImage: String,
  name: String,
  email: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .background(MaterialTheme.colorScheme.primary)
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Gap(height = 24.dp)
      CoilImage(
        imageModel = { profileImage },
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        imageOptions = ImageOptions(
          contentScale = ContentScale.Crop
        ),
        previewPlaceholder = R.drawable.ic_food_image_temp
      )
      Gap(height = 16.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = stringResource(R.string.profile_nickname),
          style = MaterialTheme.typography.titleSmall
        )
        Text(
          text = stringResource(R.string.profile_vertical_divider),
          style = MaterialTheme.typography.titleSmall
        )
        Text(
          text = name,
          style = MaterialTheme.typography.bodyMedium
        )
      }
      Gap(height = 16.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = stringResource(R.string.profile_email),
          style = MaterialTheme.typography.titleSmall
        )
        Text(
          text = stringResource(R.string.profile_vertical_divider),
          style = MaterialTheme.typography.titleSmall
        )
        Text(
          text = email,
          style = MaterialTheme.typography.bodyMedium
        )
      }
      Gap(height = 8.dp)
      Image(
        painter = painterResource(R.drawable.image_punch_hole),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
      )
      Image(
        painter = painterResource(R.drawable.image_profile_barcode),
        contentDescription = "Barcode",
        modifier = Modifier
          .fillMaxWidth()
          .offset(y = (-8).dp),
        contentScale = ContentScale.FillWidth
      )
    }
    Image(
      painter = painterResource(R.drawable.image_qr_code),
      contentDescription = "QR Code",
      modifier = Modifier
        .padding(12.dp)
        .size(64.dp)
        .align(Alignment.TopEnd)
    )
  }
}

@Composable
private fun EditProfileImageDialog(
  profileImageUri: Uri,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onProfileImageChange: (Uri) -> Unit = {},
  onImageError: () -> Unit = {},
  onConfirmation: () -> Unit = {},
  context: Context = LocalContext.current
) {
  val albumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
    if (imageUri != null) onProfileImageChange(imageUri)
    else onImageError()
  }

  @Composable
  fun ImageFromUri(
    uri: Uri,
    modifier: Modifier = Modifier
  ) {
    if (uri.toString().startsWith("http"))
      CoilImage(
        imageModel = { profileImageUri.toString() },
        modifier = modifier,
        imageOptions = ImageOptions(
          contentScale = ContentScale.Crop
        ),
        previewPlaceholder = R.drawable.ic_food_image_temp
      )
    else if (uri.toString().startsWith("content"))
      Image(
        bitmap = ImageBitmapFactory.createBitmapFromUri(context, uri).asImageBitmap(),
        contentDescription = "Profile Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    else if (File(uri.toString()).exists())
      Image(
        bitmap = BitmapFactory.decodeFile(uri.toString()).asImageBitmap(),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    else
      Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stringResource(R.string.profile_edit_profile_image_dialog_no_image),
          color = MaterialTheme.colorScheme.outline
        )
      }
  }

  LunchVoteDialog(
    title = stringResource(R.string.profile_edit_profile_image_dialog_title),
    dismissText = stringResource(R.string.profile_edit_profile_image_dialog_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.profile_edit_profile_image_dialog_save_button),
    onConfirmation = onConfirmation,
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .size(160.dp)
        .align(Alignment.CenterHorizontally),
      contentAlignment = Alignment.BottomEnd
    ) {
      ImageFromUri(
        uri = profileImageUri,
        modifier = Modifier
          .size(160.dp)
          .clip(CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
      )
      IconButton(
        onClick = { albumLauncher.launch("image/*") },
        colors = IconButtonColors(
          contentColor = MaterialTheme.colorScheme.onPrimary,
          containerColor = MaterialTheme.colorScheme.primary,
          disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
          disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
      ) {
        Icon(
          Icons.Outlined.Edit,
          contentDescription = null,
          modifier = Modifier.size(28.dp)
        )
      }
    }
  }
}

@Composable
private fun EditNameDialog(
  initialName: String,
  name: String,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onNameChange: (String) -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.profile_edit_name_dialog_title),
    dismissText = stringResource(R.string.profile_edit_name_dialog_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.profile_edit_name_dialog_save_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    confirmEnabled = (name.isEmpty() || name == initialName).not()
  ) {
    LunchVoteTextField(
      text = name,
      onTextChange = onNameChange,
      hintText = stringResource(R.string.profile_edit_name_dialog_hint_text),
      modifier = Modifier.fillMaxWidth(),
      isError = name.isEmpty() || name == initialName
    )
  }
}

@Composable
private fun DeleteUserConfirmDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = stringResource(R.string.profile_delete_user_confirm_dialog_title),
    dismissText = stringResource(R.string.profile_delete_user_confirm_dialog_cancel_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.profile_delete_user_confirm_dialog_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        Icons.Outlined.Delete,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    },
    content = {
      Text(
        text = stringResource(R.string.profile_delete_user_confirm_dialog_body),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    ProfileScreen(
      ProfileState(
        user = UserUIModel(
          email = "email@email.com",
          name = "김태우존잘"
        )
      )
    )
  }
}

@Preview
@Composable
private fun EditProfileImageDialogPreview() {
  LunchVoteTheme {
    EditProfileImageDialog(
      profileImageUri = "".toUri()
    )
  }
}

@Preview
@Composable
private fun EditNameDialogPreview() {
  LunchVoteTheme {
    EditNameDialog(
      initialName = "김태우존잘", name = ""
    )
  }
}

@Preview
@Composable
private fun DeleteUserConfirmDialogPreview() {
  LunchVoteTheme {
    DeleteUserConfirmDialog()
  }
}