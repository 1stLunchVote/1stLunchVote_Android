package com.jwd.lunchvote.presentation.ui.lounge.member

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.colorOutline
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberEvent
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberSideEffect
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberContract.LoungeMemberState
import com.jwd.lunchvote.presentation.widget.LunchVoteTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoungeMemberRoute(
  popBackStack: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LoungeMemberViewModel = hiltViewModel(),
  context: Context = LocalContext.current
) {
  val memberState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect) {
    viewModel.sideEffect.collectLatest {
      when (it) {
        is LoungeMemberSideEffect.PopBackStack -> popBackStack()
        is LoungeMemberSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  LoungeMemberScreen(
    memberState = memberState,
    modifier = modifier,
    onClickExile = { viewModel.sendEvent(LoungeMemberEvent.OnClickExile) },
    popBackStack = popBackStack
  )
}

@Composable
fun LoungeMemberScreen(
  memberState: LoungeMemberState,
  modifier: Modifier = Modifier,
  onClickExile: () -> Unit = {},
  popBackStack: () -> Unit = {}
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    LunchVoteTopBar(
      title = stringResource(id = R.string.lounge_member_topbar_title),
      popBackStack = popBackStack
    )

    Spacer(modifier = Modifier.height(32.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)
    ) {
      AsyncImage(
        model = memberState.profileUrl,
        contentDescription = "profileImage",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .border(2.dp, colorOutline, CircleShape),
      )

      Spacer(modifier = Modifier.width(32.dp))

      Text(
        text = memberState.nickname.ifBlank {
          stringResource(
            id = R.string.lounge_member_anonymous_nickname
          )
        }, modifier = Modifier.padding(top = 20.dp),
        style = MaterialTheme.typography.titleLarge
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    if (memberState.isOwner) {
      TextButton(onClick = onClickExile) {
        Text(
          text = stringResource(R.string.lounge_member_exile),
          style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.error,
            textDecoration = TextDecoration.Underline
          )
        )
      }
    }
    Spacer(modifier = Modifier.height(64.dp))

  }
}

@Preview(showBackground = true)
@Composable
private fun LoungeMemberScreenPreview() {
  LunchVoteTheme {
    LoungeMemberScreen(
      memberState = LoungeMemberState("1234", "이동건", null, true)
    )
  }
}


@Preview(showBackground = true)
@Composable
private fun LoungeMemberNotOwnerScreenPreview() {
  LunchVoteTheme {
    LoungeMemberScreen(
      memberState = LoungeMemberState("1234", "이동건", null, false)
    )
  }
}
