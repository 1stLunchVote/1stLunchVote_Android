package com.jwd.lunchvote.presentation.screen.home

import android.content.Context
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.BuildConfig
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.screen.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.screen.home.HomeContract.JoinDialogEvent
import com.jwd.lunchvote.presentation.screen.home.HomeContract.SecretDialogEvent
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.util.LocalSnackbarChannel
import com.jwd.lunchvote.presentation.util.conditional
import com.jwd.lunchvote.presentation.widget.DialogButton
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.ImageWithUploadButton
import com.jwd.lunchvote.presentation.widget.LunchVoteIcon
import com.jwd.lunchvote.presentation.widget.LunchVoteModal
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
  navigateToLounge: (String?) -> Unit,
  navigateToTemplateList: () -> Unit,
  navigateToFriendList: () -> Unit,
  navigateToSetting: () -> Unit,
  navigateToTips: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: HomeViewModel = hiltViewModel(),
  snackbarChannel: Channel<String> = LocalSnackbarChannel.current,
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is HomeSideEffect.NavigateToLounge -> navigateToLounge(it.loungeId)
        is HomeSideEffect.NavigateToTemplateList -> navigateToTemplateList()
        is HomeSideEffect.NavigateToFriendList -> navigateToFriendList()
        is HomeSideEffect.NavigateToSetting -> navigateToSetting()
        is HomeSideEffect.NavigateToTips -> navigateToTips()
        is HomeSideEffect.ShowSnackbar -> snackbarChannel.send(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(HomeEvent.ScreenInitialize) }

  state.joinDialogState?.let { dialogState ->
    JoinDialog(
      loungeId = dialogState.loungeId,
      onDismissRequest = { viewModel.sendEvent(JoinDialogEvent.OnClickCancelButton) },
      onLoungeIdChange = { viewModel.sendEvent(JoinDialogEvent.OnLoungeIdChange(it)) },
      onConfirmation = { viewModel.sendEvent(JoinDialogEvent.OnClickConfirmButton) }
    )
  }
  state.secretDialogState?.let { dialogState ->
    SecretDialog(
      foodName = dialogState.foodName,
      foodImageUri = dialogState.foodImageUri,
      onDismissRequest = { viewModel.sendEvent(SecretDialogEvent.OnClickCancelButton) },
      onFoodNameChange = { viewModel.sendEvent(SecretDialogEvent.OnFoodNameChange(it)) },
      onFoodImageChange = { viewModel.sendEvent(SecretDialogEvent.OnFoodImageChange(it)) },
      onImageError = { viewModel.sendEvent(SecretDialogEvent.OnImageLoadError) },
      onConfirmation = { viewModel.sendEvent(SecretDialogEvent.OnClickUploadButton(context)) }
    )
  }

  HomeScreen(
    state = state,
    modifier = modifier,
    onEvent = viewModel::sendEvent
  )
}

@Composable
private fun HomeScreen(
  state: HomeState,
  modifier: Modifier = Modifier,
  onEvent: (HomeEvent) -> Unit = {}
){
  Screen(
    modifier = modifier.padding(32.dp),
    topAppBar = {
      LunchVoteIcon(
        modifier = Modifier
          .padding(vertical = 8.dp)
          .conditional(BuildConfig.DEBUG) {
            pointerInput(Unit) {
              detectTapGestures(onLongPress = { onEvent(HomeEvent.OnLongPressIcon) })
            }
          },
        size = 48.dp
      )
    }
  ) {
    Gap(minHeight = 4.dp)
    FoodTrendChart(
      foodTrend = state.foodTrend,
      foodTrendRatio = state.foodTrendRatio
    )
    Gap(minHeight = 36.dp)
    HomeDivider(
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 24.dp)
    HomeButtonSet(
      modifier = Modifier.fillMaxWidth(),
      onClickLoungeButton = { onEvent(HomeEvent.OnClickLoungeButton) },
      onClickJoinLoungeButton = { onEvent(HomeEvent.OnClickJoinLoungeButton) },
      onClickTemplateButton = { onEvent(HomeEvent.OnClickTemplateButton) },
      onClickFriendButton = { onEvent(HomeEvent.OnClickFriendButton) },
      onClickSettingButton = { onEvent(HomeEvent.OnClickSettingButton) },
      onClickTipsButton = { onEvent(HomeEvent.OnClickTipsButton) },
    )
  }
}

@Composable
private fun FoodTrendChart(
  foodTrend: FoodUIModel?,
  foodTrendRatio: Float,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = stringResource(R.string.home_banner_title),
      style = MaterialTheme.typography.titleMedium
    )
    Gap(height = 24.dp)
    Box(
      modifier = Modifier.size(192.dp),
      contentAlignment = Alignment.Center
    ) {
      CircularChart(
        foodTrendRatio = foodTrendRatio
      )
      if (foodTrend == null) {
        Box(
          modifier = Modifier.size(192.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stringResource(R.string.home_banner_no_trend_food),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
          )
        }
      } else {
        CoilImage(
          imageModel = { foodTrend.imageUrl },
          modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .border(4.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
          previewPlaceholder = R.drawable.ic_food_image_temp
        )
      }
    }
    Gap(height = 16.dp)
    Text(
      text = foodTrend?.name ?: "",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary
    )
    Gap(height = 8.dp)
    Text(
      text = stringResource(R.string.home_banner_score, foodTrendRatio.toInt()),
      modifier = Modifier.alpha(if (foodTrend == null) 0f else 1f),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.outline
    )
  }
}

@Composable
private fun CircularChart(
  foodTrendRatio: Float,
  modifier: Modifier = Modifier,
  size: Dp = 192.dp,
  thickness: Dp = 8.dp,
  color: Color = MaterialTheme.colorScheme.primary,
  backgroundCircleColor: Color = MaterialTheme.colorScheme.background
) {
  val animatedFoodTrendRatio by animateFloatAsState(
    targetValue = foodTrendRatio,
    animationSpec = tween(
      durationMillis = 1000,
      delayMillis = 1000,
      easing = LinearEasing
    ),
    label = "AnimatedFoodTrendRatio"
  )
  val sweepAngle = 360 * animatedFoodTrendRatio / 100

  Canvas(
    modifier = modifier.size(size)
  ) {
    val arcRadius = size.toPx()
    drawCircle(
      color = backgroundCircleColor,
      radius = arcRadius / 2,
      style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
    )
    drawArc(
      color = color,
      startAngle = -90f,
      sweepAngle = sweepAngle,
      useCenter = false,
      style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round),
      size = Size(arcRadius, arcRadius),
      topLeft = Offset(
        x = (size.toPx() - arcRadius) / 2,
        y = (size.toPx() - arcRadius) / 2
      )
    )
  }
}

@Composable
private fun HomeDivider(
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy((-1).dp)
  ) {
    Image(
      painterResource(R.drawable.ic_triangle),
      contentDescription = null
    )
    HorizontalDivider(
      modifier = Modifier.weight(1f),
      thickness = 2.dp,
      color = MaterialTheme.colorScheme.primary
    )
    Image(
      painterResource(R.drawable.ic_triangle),
      contentDescription = null,
      modifier = Modifier.graphicsLayer(rotationZ = 180f)
    )
  }
}

@Composable
private fun HomeButtonSet(
  modifier: Modifier = Modifier,
  onClickLoungeButton: () -> Unit = {},
  onClickJoinLoungeButton: () -> Unit = {},
  onClickTemplateButton: () -> Unit = {},
  onClickFriendButton: () -> Unit = {},
  onClickSettingButton: () -> Unit = {},
  onClickTipsButton: () -> Unit = {}
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(128.dp)
          .clip(MaterialTheme.shapes.medium)
          .background(MaterialTheme.colorScheme.primary)
          .clickable { onClickLoungeButton() }
      ) {
        Text(
          text = stringResource(R.string.home_start_button),
          modifier = Modifier.padding(top = 16.dp, start = 24.dp),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onPrimary
        )
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .height(128.dp)
          .clip(MaterialTheme.shapes.medium)
          .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
          .clickable { onClickJoinLoungeButton() }
      ) {
        Text(
          text = stringResource(R.string.home_join_button),
          modifier = Modifier.padding(top = 16.dp, start = 24.dp),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(64.dp)
          .clip(MaterialTheme.shapes.medium)
          .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
          .clickable { onClickTemplateButton() },
        contentAlignment = Alignment.TopEnd
      ) {
        Text(
          text = stringResource(R.string.home_template_button),
          modifier = Modifier.padding(top = 16.dp, end = 24.dp),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(MaterialTheme.shapes.medium)
          .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
          .clickable { onClickFriendButton() },
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "친구\n관리",
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(MaterialTheme.shapes.medium)
          .background(MaterialTheme.colorScheme.outline)
          .clickable { onClickSettingButton() },
        contentAlignment = Alignment.Center
      ) {
        Image(
          painterResource(R.drawable.ic_gear),
          contentDescription = "Setting Button"
        )
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .clip(MaterialTheme.shapes.medium)
          .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
          .clickable { onClickTipsButton() },
        contentAlignment = Alignment.CenterEnd
      ) {
        Text(
          text = stringResource(R.string.home_tips_button),
          modifier = Modifier.padding(end = 24.dp),
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
  }
}

@Composable
private fun JoinDialog(
  loungeId: String,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onLoungeIdChange: (String) -> Unit = {},
  onConfirmation: () -> Unit = {},
) {
  LunchVoteModal(
    title = stringResource(R.string.join_dialog_title),
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    icon = { LunchVoteIcon() },
    body = stringResource(R.string.join_dialog_body),
    closable = true,
    content = {
      LunchVoteTextField(
        text = loungeId,
        onTextChange = onLoungeIdChange,
        hintText = stringResource(R.string.join_dialog_hint_text)
      )
    },
    buttons = {
      DialogButton(
        text = stringResource(R.string.join_dialog_dismiss_button),
        onClick = onDismissRequest,
        isDismiss = true
      )
      DialogButton(
        text = stringResource(R.string.join_dialog_confirm_button),
        onClick = onConfirmation
      )
    }
  )
}

@Preview
@Composable
private fun Preview() {
  ScreenPreview {
    HomeScreen(
      HomeState(
        foodTrend = FoodUIModel(
          name = "햄버거"
        ),
        foodTrendRatio = 80f
      )
    )
  }
}

@Preview
@Composable
private fun JoinDialogPreview() {
  LunchVoteTheme {
    JoinDialog("1234")
  }
}

// TODO: Temporary Secret Dialog
@Composable
private fun SecretDialog(
  foodName: String,
  foodImageUri: Uri,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit = {},
  onFoodNameChange: (String) -> Unit = {},
  onFoodImageChange: (Uri) -> Unit = {},
  onImageError: () -> Unit = {},
  onConfirmation: () -> Unit = {}
) {
  LunchVoteModal(
    title = "음식 추가",
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    icon = { LunchVoteIcon() },
    body = "음식을 추가해주세요. (개발자 전용)",
    closable = true,
    content = {
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        ImageWithUploadButton(
          uri = foodImageUri,
          onImageChange = onFoodImageChange,
          onError = onImageError
        )
        LunchVoteTextField(
          text = foodName,
          onTextChange = onFoodNameChange,
          hintText = "음식 이름"
        )
      }
    },
    buttons = {
      DialogButton(
        text = "취소",
        onClick = onDismissRequest,
        isDismiss = true
      )
      DialogButton(
        text = "추가",
        onClick = onConfirmation
      )
    }
  )
}

@Preview
@Composable
private fun SecretDialogPreview() {
  LunchVoteTheme {
    SecretDialog(
      foodName = "햄버거",
      foodImageUri = Uri.EMPTY
    )
  }
}