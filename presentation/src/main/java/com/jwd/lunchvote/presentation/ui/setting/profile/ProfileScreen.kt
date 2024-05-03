package com.jwd.lunchvote.presentation.ui.setting.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileEvent
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileSideEffect
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileContract.ProfileState
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileRoute(
  popBackStack: () -> Unit,
  navigateToLogin: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  viewModel: ProfileViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when(it) {
        is ProfileSideEffect.PopBackStack -> popBackStack()
        is ProfileSideEffect.OpenEditProfileImageDialog -> viewModel.setDialogState(ProfileContract.EDIT_PROFILE_IMAGE_DIALOG)
        is ProfileSideEffect.OpenEditNameDialog -> viewModel.setDialogState(ProfileContract.EDIT_NAME_DIALOG)
        is ProfileSideEffect.OpenDeleteUserConfirmDialog -> viewModel.setDialogState(ProfileContract.DELETE_USER_CONFIRM_DIALOG)
        is ProfileSideEffect.CloseDialog -> viewModel.setDialogState("")
        is ProfileSideEffect.NavigateToLogin -> navigateToLogin()
        is ProfileSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  when (dialogState) {
    ProfileContract.EDIT_PROFILE_IMAGE_DIALOG -> {
      // EditProfileImageDialog
    }
    ProfileContract.EDIT_NAME_DIALOG -> {
      // EditNameDialog
    }
  }

  if (loading) LoadingScreen()
  else ProfileScreen(
    state = state,
    onClickBackButton = { viewModel.sendEvent(ProfileEvent.OnClickBackButton) },
    onClickEditProfileImageButton = { viewModel.sendEvent(ProfileEvent.OnClickEditProfileImageButton) },
    onClickEditNameButton = { viewModel.sendEvent(ProfileEvent.OnClickEditNameButton) },
    onClickDeleteUserButton = { viewModel.sendEvent(ProfileEvent.OnClickDeleteUserButton) }
  )
}

@Composable
private fun ProfileScreen(
  state: ProfileState,
  modifier: Modifier = Modifier,
  onClickBackButton: () -> Unit = {},
  onClickEditProfileImageButton: () -> Unit = {},
  onClickEditNameButton: () -> Unit = {},
  onClickDeleteUserButton: () -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = stringResource(R.string.profile_title),
        navIconVisible = true,
        popBackStack = onClickBackButton
      )
    },
    scrollable = false
  ) {
    ProfileTicket(
      profileImageUrl = state.profileImageUrl,
      name = state.name,
      email = state.user.email,
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterHorizontally)
    ) {
      TextButton(onClickEditProfileImageButton) {
        Text(
          text = stringResource(R.string.profile_edit_profile_image),
          textDecoration = TextDecoration.Underline
        )
      }
      TextButton(onClickEditNameButton) {
        Text(
          text = stringResource(R.string.profile_edit_name),
          textDecoration = TextDecoration.Underline
        )
      }
    }
    Gap(minHeight = 32.dp)
    TextButton(onClickDeleteUserButton) {
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
  profileImageUrl: String,
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
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Gap(height = 24.dp)
      CoilImage(
        imageModel = { profileImageUrl },
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
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
        modifier = Modifier.fillMaxWidth(),
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

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    ProfileScreen(
      ProfileState(
        user = UserUIModel(
          email = "email@email.com",
          name = "김태우존잘"
        ),
        profileImageUrl = UserUIModel().profileImageUrl,
        name = "김태우존잘"
      )
    )
  }
}