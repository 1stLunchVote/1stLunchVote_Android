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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.widget.Gap
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import com.jwd.lunchvote.presentation.widget.Screen
import com.jwd.lunchvote.presentation.widget.ScreenPreview
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
  navigateToLounge: () -> Unit,
  navigateToTemplateList: () -> Unit,
  navigateToSetting: () -> Unit,
  navigateToTips: () -> Unit,
  navigateToTest: () -> Unit,
  navigateToFirstVote: () -> Unit,
  openJoinDialog: () -> Unit,
  showSnackBar: suspend (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: HomeViewModel = hiltViewModel(),
  context: Context = LocalContext.current
){
  val homeState by viewModel.viewState.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.sideEffect){
    viewModel.sideEffect.collectLatest {
      when(it){
        is HomeSideEffect.NavigateToLounge -> navigateToLounge()
        is HomeSideEffect.NavigateToTemplateList -> navigateToTemplateList()
        is HomeSideEffect.NavigateToSetting -> navigateToSetting()
        is HomeSideEffect.NavigateToTips -> navigateToTips()
        is HomeSideEffect.OpenJoinDialog -> openJoinDialog()
        is HomeSideEffect.ShowSnackBar -> showSnackBar(it.message.asString(context))
      }
    }
  }

  if (isLoading) LoadingScreen()
  else HomeScreen(
    homeState = homeState,
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
  homeState: HomeState,
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
        .align(CenterHorizontally)
    )
    Gap(minHeight = 36.dp)
    FoodTrendChart(
      foodTrend = homeState.foodTrend,
      foodTrendRatio = homeState.foodTrendRatio,
      modifier = Modifier.align(CenterHorizontally)
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
    horizontalAlignment = CenterHorizontally
  ) {
    Text(
      stringResource(R.string.home_banner_title),
      style = MaterialTheme.typography.titleMedium
    )
    Gap(height = 24.dp)
    Box(
      modifier = Modifier.size(192.dp),
      contentAlignment = Center
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
          .clip(RoundedCornerShape(16.dp))
          .background(
            MaterialTheme.colorScheme.primary,
            RoundedCornerShape(16.dp)
          )
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
          .clip(RoundedCornerShape(16.dp))
          .border(
            2.dp,
            MaterialTheme.colorScheme.primary,
            RoundedCornerShape(16.dp)
          )
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
        .clip(RoundedCornerShape(16.dp))
        .border(
          2.dp,
          MaterialTheme.colorScheme.primary,
          RoundedCornerShape(16.dp)
        )
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
          .clip(RoundedCornerShape(16.dp))
          .background(
            MaterialTheme.colorScheme.outline,
            RoundedCornerShape(16.dp)
          )
          .clickable { onClickSettingButton() },
        contentAlignment = Center
      ) {
        Image(
          painterResource(R.drawable.ic_gear),
          null
        )
      }
      ConstraintLayout(
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .clip(RoundedCornerShape(16.dp))
          .border(
            2.dp,
            MaterialTheme.colorScheme.outline,
            RoundedCornerShape(16.dp)
          )
          .clickable { onClickTipsButton() }
      ) {
        val text = createRef()
        Text(
          stringResource(R.string.home_tips_button),
          modifier = Modifier.constrainAs(text) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, margin = 24.dp)
          },
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
  }
}

@Preview
@Composable
private fun HomeScreenPreview() {
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