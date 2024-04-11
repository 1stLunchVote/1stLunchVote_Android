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
import androidx.compose.foundation.layout.fillMaxSize
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
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeEvent
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeSideEffect
import com.jwd.lunchvote.presentation.ui.home.HomeContract.HomeState
import com.jwd.lunchvote.presentation.widget.LoadingScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
  navigateToLounge: (String?) -> Unit,
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
        is HomeSideEffect.NavigateToLounge -> navigateToLounge(it.loungeId)
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
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 32.dp),
    horizontalAlignment = CenterHorizontally
  ) {
    Image(
      painterResource(R.drawable.ic_logo),
      null,
      modifier = Modifier
        .size(48.dp)
        .align(CenterHorizontally)
    )
    FoodTrendChart(
      Modifier.padding(top = 40.dp, bottom = 40.dp)
    )
    HomeDivider(
      Modifier
        .fillMaxWidth()
        .padding(vertical = 24.dp)
    )
    HomeButtonSet(
      onClickLoungeButton = onClickLoungeButton,
      onClickJoinLoungeButton = onClickJoinLoungeButton,
      onClickTemplateButton = onClickTemplateButton,
      onClickSettingButton = onClickSettingButton,
      onClickTipsButton = onClickTipsButton,
    )
  }
}

@Composable
fun FoodTrendChart(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = CenterHorizontally
  ) {
    Text(
      stringResource(R.string.home_banner_title),
      style = MaterialTheme.typography.titleMedium
    )
    Spacer(Modifier.height(16.dp))
    Box(
      modifier = Modifier.size(192.dp),
      contentAlignment = Center
    ) {
      CircularChart()
      Image(
        painterResource(R.drawable.ic_food_image_temp),
        null,
        modifier = Modifier
          .size(160.dp)
          .clip(CircleShape)
          .border(4.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
      )
    }
    Text(
      "햄버거",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(4.dp))
    Text(
      text = stringResource(R.string.home_banner_score, 36),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.outline
    )
  }
}

@Composable
fun CircularChart(
  value: Float = 36f,
  color: Color = MaterialTheme.colorScheme.primary,
  backgroundCircleColor: Color = MaterialTheme.colorScheme.background,
  size: Dp = 192.dp,
  thickness: Dp = 8.dp
) {
  val sweepAngle = 360 * value / 100

  Canvas(
    modifier = Modifier
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
fun HomeDivider(
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
fun HomeButtonSet(
  onClickLoungeButton: () -> Unit = {},
  onClickJoinLoungeButton: () -> Unit = {},
  onClickTemplateButton: () -> Unit = {},
  onClickSettingButton: () -> Unit = {},
  onClickTipsButton: () -> Unit = {},
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
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
          stringResource(R.string.home_start_btn),
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
          stringResource(R.string.home_join_btn),
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
        stringResource(R.string.home_template_btn),
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
          stringResource(R.string.home_tips_btn),
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

@Preview(showBackground = false, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
  LunchVoteTheme {
    HomeScreen(
      HomeState()
    )
  }
}