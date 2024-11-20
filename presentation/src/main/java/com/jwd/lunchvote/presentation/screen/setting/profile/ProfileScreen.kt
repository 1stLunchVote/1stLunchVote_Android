package com.jwd.lunchvote.presentation.screen.setting.profile

import android.content.Context
import android.net.Uri
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
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.DeleteDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.NameDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileImageDialogEvent
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileSideEffect
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileContract.ProfileState
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.widget.Dialog
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.ImageWithUploadButton
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.jwd.lunchvote.presentation.widget.TextField
import com.jwd.lunchvote.presentation.widget.TopBar
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

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

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is ProfileSideEffect.PopBackStack -> popBackStack()
        is ProfileSideEffect.NavigateToLogin -> navigateToLogin()
        is ProfileSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(ProfileEvent.ScreenInitialize) }

  state.profileImageDialogState?.let { dialogState ->
    ProfileImageDialog(
      profileImageUri = dialogState.profileImageUri,
      onEvent = viewModel::sendEvent
    )
  }
  state.nameDialogState?.let { dialogState ->
    NameDialog(
      name = dialogState.name,
      onEvent = viewModel::sendEvent
    )
  }
  state.deleteDialogState?.let{
    DeleteDialog(onEvent = viewModel::sendEvent)
  }


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
    modifier = modifier.padding(24.dp),
    topAppBar = {
      TopBar(
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
      modifier = Modifier.fillMaxWidth()
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
      onClick = { onEvent(ProfileEvent.OnClickDeleteUserButton) },
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text(
        text = stringResource(R.string.profile_delete_user),
        color = MaterialTheme.colorScheme.error,
        textDecoration = TextDecoration.Underline
      )
    }
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
private fun ProfileImageDialog(
  profileImageUri: Uri,
  modifier: Modifier = Modifier,
  onEvent: (ProfileImageDialogEvent) -> Unit = {},
  context: Context = LocalContext.current
) {
  Dialog(
    title = stringResource(R.string.p_profile_image_dialog_title),
    onDismissRequest = { onEvent(ProfileImageDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.p_profile_image_dialog_body),
    closable = false,
    content = {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        ImageWithUploadButton(
          uri = profileImageUri,
          onImageChange = { onEvent(ProfileImageDialogEvent.OnProfileImageChange(it)) },
          onError = { onEvent(ProfileImageDialogEvent.OnImageLoadError) }
        )
      }
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.p_profile_image_dialog_cancel_button),
        onClick = { onEvent(ProfileImageDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.p_profile_image_dialog_save_button),
        onClick = { onEvent(ProfileImageDialogEvent.OnClickSaveButton(context)) }
      )
    }
  )
}

@Composable
private fun NameDialog(
  name: String,
  modifier: Modifier = Modifier,
  onEvent: (NameDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.p_name_dialog_title),
    onDismissRequest = { onEvent(NameDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Build,
        contentDescription = "Build"
      )
    },
    iconColor = MaterialTheme.colorScheme.tertiary,
    body = stringResource(R.string.p_name_dialog_body),
    closable = false,
    content = {
      TextField(
        text = name,
        onTextChange = { onEvent(NameDialogEvent.OnNameChange(it)) },
        hintText = stringResource(R.string.p_name_dialog_hint_text),
        modifier = Modifier.fillMaxWidth(),
        isError = name.isEmpty()
      )
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.p_name_dialog_cancel_button),
        onClick = { onEvent(NameDialogEvent.OnClickCancelButton) },
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.p_name_dialog_save_button),
        onClick = { onEvent(NameDialogEvent.OnClickSaveButton) },
        enabled = name.isNotEmpty()
      )
    }
  )
}

@Composable
private fun DeleteDialog(
  modifier: Modifier = Modifier,
  onEvent: (ProfileContract.DeleteDialogEvent) -> Unit = {}
) {
  Dialog(
    title = stringResource(R.string.p_delete_dialog_title),
    onDismissRequest = { onEvent(DeleteDialogEvent.OnClickCancelButton) },
    modifier = modifier,
    icon = {
      Icon(
        Icons.Rounded.Delete,
        contentDescription = "Delete"
      )
    },
    iconColor = MaterialTheme.colorScheme.error,
    body = stringResource(R.string.c_delete_dialog_body),
    closable = false,
    buttons = {
      DialogButton(
        text = stringResource(R.string.p_delete_dialog_cancel_button),
        onClick = { onEvent(DeleteDialogEvent.OnClickCancelButton) },
        isDismiss = true,
        color = MaterialTheme.colorScheme.onSurface
      )
      DialogButton(
        text = stringResource(R.string.p_delete_dialog_delete_button),
        onClick = { onEvent(DeleteDialogEvent.OnClickDeleteButton) },
        color = MaterialTheme.colorScheme.error
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
private fun ProfileImageDialogPreview() {
  LunchVoteTheme {
    ProfileImageDialog(
      profileImageUri = "".toUri()
    )
  }
}

@Preview
@Composable
private fun NameDialogPreview() {
  LunchVoteTheme {
    NameDialog(
      name = "김태우존잘"
    )
  }
}

@Preview
@Composable
private fun DeleteDialogPreview() {
  LunchVoteTheme {
    DeleteDialog()
  }
}