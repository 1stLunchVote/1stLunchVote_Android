package com.jwd.lunchvote.presentation.ui.home

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog
import com.jwd.lunchvote.presentation.widget.LunchVoteTextField
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
  navigateToLounge: (String?) -> Unit,
  navigateToTemplateList: () -> Unit,
  navigateToSetting: () -> Unit,
  navigateToTips: () -> Unit,
  navigateToTest: () -> Unit,
  navigateToFirstVote: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: HomeViewModel = hiltViewModel(),
  context: Context = LocalContext.current
){
  val state by viewModel.viewState.collectAsStateWithLifecycle()
  val loading by viewModel.isLoading.collectAsStateWithLifecycle()
  val dialog by viewModel.dialogState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is HomeSideEffect.NavigateToLounge -> navigateToLounge(it.loungeId)
        is HomeSideEffect.NavigateToTemplateList -> navigateToTemplateList()
        is HomeSideEffect.NavigateToSetting -> navigateToSetting()
        is HomeSideEffect.NavigateToTips -> navigateToTips()
        is HomeSideEffect.OpenJoinDialog -> viewModel.setDialogState(HomeContract.JOIN_DIALOG)
        is HomeSideEffect.CloseDialog -> viewModel.setDialogState("")
        is HomeSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  LaunchedEffect(Unit) { viewModel.sendEvent(HomeEvent.ScreenInitialize) }

  when (dialog) {
    HomeContract.JOIN_DIALOG -> {
      JoinDialog(
        loungeId = state.loungeId ?: "",
        onDismissRequest = { viewModel.sendEvent(HomeEvent.OnClickCancelButtonJoinDialog) },
        onLoungeIdChanged = { viewModel.sendEvent(HomeEvent.OnLoungeIdChanged(it)) },
        onConfirmation = { viewModel.sendEvent(HomeEvent.OnClickConfirmButtonJoinDialog) }
      )
    }
  }

  if (loading) LoadingScreen()
  else HomeScreen(
    state = state,
    modifier = modifier,
    onClickLoungeButton = { viewModel.sendEvent(HomeEvent.OnClickLoungeButton) },
    onClickJoinLoungeButton = { viewModel.sendEvent(HomeEvent.OnClickJoinLoungeButton) },
    onClickTemplateButton = { viewModel.sendEvent(HomeEvent.OnClickTemplateButton) },
    onClickSettingButton = { viewModel.sendEvent(HomeEvent.OnClickSettingButton) },
    onClickTipsButton = { viewModel.sendEvent(HomeEvent.OnClickTipsButton) }
  )

  Row {
    Button(onClick = navigateToFirstVote) {
      Text("1차 투표 테스트")
    }
    Button(onClick = navigateToTest) {
      Text(text = "2차 투표 화면 테스트")
    }
  }
}

@Composable
private fun HomeScreen(
  state: HomeState,
  modifier: Modifier = Modifier,
  onClickLoungeButton: () -> Unit = {},
  onClickJoinLoungeButton: () -> Unit = {},
  onClickTemplateButton: () -> Unit = {},
  onClickSettingButton: () -> Unit = {},
  onClickTipsButton: () -> Unit = {}
){
  Screen(
    modifier = modifier.padding(horizontal = 32.dp)
  ) {
    Image(
      painterResource(R.drawable.ic_logo),
      null,
      modifier = Modifier
        .size(48.dp)
        .align(Alignment.CenterHorizontally)
    )
    Gap(minHeight = 36.dp)
    FoodTrendChart(
      foodTrend = state.foodTrend,
      foodTrendRatio = state.foodTrendRatio,
      modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Gap(minHeight = 36.dp)
    HomeDivider(
      modifier = Modifier.fillMaxWidth()
    )
    Gap(height = 24.dp)
    HomeButtonSet(
      modifier = Modifier.fillMaxWidth(),
      onClickLoungeButton = onClickLoungeButton,
      onClickJoinLoungeButton = onClickJoinLoungeButton,
      onClickTemplateButton = onClickTemplateButton,
      onClickSettingButton = onClickSettingButton,
      onClickTipsButton = onClickTipsButton,
    )
    Gap(height = 64.dp)
  }
}

@Composable
private fun FoodTrendChart(
  foodTrend: FoodUIModel,
  foodTrendRatio: Float,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      stringResource(R.string.home_banner_title),
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
      CoilImage(
        imageModel = { foodTrend.imageUrl },
        modifier = Modifier
          .size(160.dp)
          .clip(CircleShape)
          .border(4.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        previewPlaceholder = R.drawable.ic_food_image_temp
      )
    }
    Gap(height = 16.dp)
    Text(
      foodTrend.name,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary
    )
    Gap(height = 8.dp)
    Text(
      text = stringResource(R.string.home_banner_score, foodTrendRatio.toInt()),
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
  val sweepAngle = 360 * foodTrendRatio / 100

  Canvas(
    modifier = modifier
      .size(size)
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
      null
    )
    HorizontalDivider(
      modifier = Modifier.weight(1f),
      thickness = 2.dp,
      color = MaterialTheme.colorScheme.primary
    )
    Image(
      painterResource(R.drawable.ic_triangle),
      null,
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
  onClickSettingButton: () -> Unit = {},
  onClickTipsButton: () -> Unit = {},
) {
  val buttonShape = RoundedCornerShape(16.dp)

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
          .clip(buttonShape)
          .background(MaterialTheme.colorScheme.primary)
          .clickable { onClickLoungeButton() }
      ) {
        Text(
          stringResource(R.string.home_start_button),
          modifier = Modifier.padding(top = 16.dp, start = 24.dp),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onPrimary
        )
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .height(128.dp)
          .clip(buttonShape)
          .border(2.dp, MaterialTheme.colorScheme.primary, buttonShape)
          .clickable { onClickJoinLoungeButton() }
      ) {
        Text(
          stringResource(R.string.home_join_button),
          modifier = Modifier.padding(top = 16.dp, start = 24.dp),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .clip(buttonShape)
        .border(2.dp, MaterialTheme.colorScheme.primary, buttonShape)
        .clickable { onClickTemplateButton() },
      contentAlignment = Alignment.TopEnd
    ) {
      Text(
        stringResource(R.string.home_template_button),
        modifier = Modifier.padding(top = 16.dp, end = 24.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
      )
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(buttonShape)
          .background(MaterialTheme.colorScheme.outline)
          .clickable { onClickSettingButton() },
        contentAlignment = Alignment.Center
      ) {
        Image(
          painterResource(R.drawable.ic_gear),
          "Setting Button"
        )
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .clip(buttonShape)
          .border(2.dp, MaterialTheme.colorScheme.outline, buttonShape)
          .clickable { onClickTipsButton() },
        contentAlignment = Alignment.CenterEnd
      ) {
        Text(
          stringResource(R.string.home_tips_button),
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
  onLoungeIdChanged: (String) -> Unit = {},
  onConfirmation: () -> Unit = {},
) {
  LunchVoteDialog(
    title = stringResource(R.string.join_dialog_title),
    dismissText = stringResource(R.string.join_dialog_dismiss_button),
    onDismissRequest = onDismissRequest,
    confirmText = stringResource(R.string.join_dialog_confirm_button),
    onConfirmation = onConfirmation,
    modifier = modifier,
    confirmEnabled = loungeId.isNotBlank()
  ) {
    LunchVoteTextField(
      text = loungeId,
      onTextChange = onLoungeIdChanged,
      hintText = stringResource(R.string.join_dialog_hint_text)
    )
  }
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