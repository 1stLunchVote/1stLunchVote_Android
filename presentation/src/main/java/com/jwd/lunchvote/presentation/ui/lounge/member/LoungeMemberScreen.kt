package com.jwd.lunchvote.presentation.ui.lounge.member

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.UserUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberEvent
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberState
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoungeMemberRoute(
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoungeMemberViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeMemberSideEffect.PopBackStack -> popBackStack()
        is LoungeMemberSideEffect.OpenExileConfirmDialog -> viewModel.openDialog(LoungeMemberContract.EXILE_CONFIRM_DIALOG)
        is LoungeMemberSideEffect.CloseDialog -> viewModel.openDialog("")
        is LoungeMemberSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  when (dialog) {
    LoungeMemberContract.EXILE_CONFIRM_DIALOG -> {
      ExileConfirmDialog(
        onDismissRequest = { viewModel.sendEvent(LoungeMemberEvent.OnClickCancelButtonExileConfirmDialog) },
        onConfirmation = { viewModel.sendEvent(LoungeMemberEvent.OnClickConfirmButtonExileConfirmDialog) }
      )
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(LoungeMemberEvent.ScreenInitialize) }

  if (loading) LoadingScreen()
  else LoungeMemberScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun LoungeMemberScreen(
  state: LoungeMemberState,
  modifier: Modifier = Modifier,
  onEvent: (LoungeMemberEvent) -> Unit = {}
) {
  Screen(
    modifier = modifier,
    topAppBar = {
      LunchVoteTopBar(
        title = "참여자 정보",
        popBackStack = { onEvent(LoungeMemberEvent.OnClickBackButton) }
      )
    }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalArrangement = Arrangement.spacedBy(32.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CoilImage(
        imageModel = { state.user.profileImage },
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        imageOptions = ImageOptions(
          contentScale = ContentScale.Crop
        ),
        previewPlaceholder = R.drawable.ic_food_image_temp
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = state.user.name,
          style = MaterialTheme.typography.titleLarge
        )
        Text(
          text = state.user.email,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
    Gap(minHeight = 32.dp)
    if (state.member.status == MemberStatusUIType.OWNER && state.isMe.not()) TextButton(
      onClick = { onEvent(LoungeMemberEvent.OnClickExileButton) },
      modifier = Modifier
        .padding(64.dp)
        .align(Alignment.CenterHorizontally)
    ) {
      Text(
        text = "추방하기",
        color = MaterialTheme.colorScheme.error,
        textDecoration = TextDecoration.Underline
      )
    }
  }
}

@Composable
private fun ExileConfirmDialog(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteDialog(
    title = "추방하시겠습니까?",
    dismissText = "취소",
    onDismissRequest = onDismissRequest,
    confirmText = "추방",
    onConfirmation = onConfirmation,
    modifier = modifier,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Warning,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    }
  ) {
    Text(
      text = "추방된 사용자는 해당 방에 다시 들어올 수 없습니다.",
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    LoungeMemberScreen(
      LoungeMemberState(
        user = UserUIModel(
          name = "김철수",
          email = "email@email.com"
        )
      )
    )
  }
}

@Preview
@Composable
private fun ExileConfirmDialogPreview() {
  LunchVoteTheme {
    ExileConfirmDialog()
  }
}