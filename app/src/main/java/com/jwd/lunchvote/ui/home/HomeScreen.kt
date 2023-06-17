package com.jwd.lunchvote.ui.home

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.home.HomeContract.*
import com.jwd.lunchvote.ui.login.LoginContract
import com.jwd.lunchvote.util.loginWithKakao
import com.jwd.lunchvote.widget.LunchVoteTextField
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun HomeRoute(
    navigateToLounge: () -> Unit,
    navigateToTemplate: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToTips: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
){
    val homeState : HomeState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is HomeSideEffect.NavigateToLounge -> {
                    navigateToLounge()
                }
                is HomeSideEffect.NavigateToTemplate -> {
                    navigateToTemplate()
                }
                is HomeSideEffect.NavigateToSetting -> {
                    navigateToSetting()
                }
                is HomeSideEffect.NavigateToTips -> {
                    navigateToTips()
                }
                is HomeSideEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    HomeScreen(
        homeState = homeState,
        snackBarHostState = snackBarHostState,
        onClickLoungeButton = { viewModel.sendEvent(HomeEvent.OnClickLoungeButton) },
        onClickJoinLoungeButton = { viewModel.sendEvent(HomeEvent.OnClickJoinLoungeButton) },
        onClickDismissButtonOfJoinDialog = { viewModel.sendEvent(HomeEvent.OnClickDismissButtonOfJoinDialog) },
        onCodeChanged = { code -> viewModel.sendEvent(HomeEvent.SetJoinCode(code)) },
        onClickTemplateButton = { viewModel.sendEvent(HomeEvent.OnClickTemplateButton) },
        onClickSettingButton = { viewModel.sendEvent(HomeEvent.OnClickSettingButton) },
        onClickTipsButton = { viewModel.sendEvent(HomeEvent.OnClickTipsButton) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    homeState: HomeState,
    snackBarHostState: SnackbarHostState,
    onClickLoungeButton: () -> Unit = {},
    onClickJoinLoungeButton: () -> Unit = {},
    onClickDismissButtonOfJoinDialog: () -> Unit = {},
    onCodeChanged: (String) -> Unit = {},
    onClickTemplateButton: () -> Unit = {},
    onClickSettingButton: () -> Unit = {},
    onClickTipsButton: () -> Unit = {},
){
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
            Spacer(Modifier.height(40.dp))
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
            Spacer(Modifier.height(64.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((-1).dp)
            ) {
                Image(
                    painterResource(R.drawable.ic_triangle),
                    null
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Image(
                    painterResource(R.drawable.ic_triangle),
                    null,
                    modifier = Modifier
                        .graphicsLayer(rotationZ = 180f)
                )
            }
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConstraintLayout(
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
                        val text = createRef()
                        Text(
                            stringResource(R.string.home_start_btn),
                            modifier = Modifier.constrainAs(text) {
                                top.linkTo(parent.top, margin = 16.dp)
                                start.linkTo(parent.start, margin = 24.dp)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    ConstraintLayout(
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
                        val text = createRef()
                        Text(
                            stringResource(R.string.home_join_btn),
                            modifier = Modifier.constrainAs(text) {
                                top.linkTo(parent.top, margin = 16.dp)
                                start.linkTo(parent.start, margin = 24.dp)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onClickTemplateButton() }
                ) {
                    val text = createRef()
                    Text(
                        stringResource(R.string.home_template_btn),
                        modifier = Modifier.constrainAs(text) {
                            top.linkTo(parent.top, margin = 16.dp)
                            end.linkTo(parent.end, margin = 24.dp)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onClickSettingButton() }
                    ) {
                        val setting = createRef()
                        Image(
                            painterResource(R.drawable.ic_gear),
                            null,
                            modifier = Modifier.constrainAs(setting) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
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
        if (homeState.showJoinDialog) {
            JoinDialog(
                homeState = homeState,
                onCodeChanged = onCodeChanged,
                dismiss = onClickDismissButtonOfJoinDialog
            )
        }
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JoinDialog(
    homeState: HomeState,
    onCodeChanged: (String) -> Unit = {},
    dismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            Button(
                onClick = { /*TODO: 초대 코드를 통해 투표 방 참여하기*/ },
                enabled = false
            ) { Text("참여") }
        },
        dismissButton = { Button(dismiss) { Text("취소") } },
        title = { Text(stringResource(R.string.home_join_btn)) },
        text = {
            LunchVoteTextField(
                text = homeState.code,
                hintText = stringResource(R.string.home_join_code_hint),
                onTextChanged = onCodeChanged
            )
        }
    )
}

@Preview(showBackground = false)
@Composable
fun HomeScreenPreview() {
    LunchVoteTheme {
        HomeScreen(
            homeState = HomeState(),
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JoinDialogPreview() {
    LunchVoteTheme {
        JoinDialog(homeState = HomeState())
    }
}