package com.jwd.lunchvote.ui.lounge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.colorOutlineVariant
import com.jwd.lunchvote.model.LoungeMember
import com.jwd.lunchvote.ui.lounge.LoungeContract.*
import com.jwd.lunchvote.widget.LunchVoteTopBar

@Composable
fun LoungeRoute(
    viewModel: LoungeViewModel = hiltViewModel(),
    popBackStack: () -> Unit
){
    val loungeState : LoungeState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LoungeScreen(
        loungeState = loungeState,
        snackBarHostState = snackBarHostState,
        popBackStack = popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoungeScreen(
    loungeState: LoungeState,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit = {}
){
    Scaffold(
        topBar = {
            LunchVoteTopBar(
                title = stringResource(id = R.string.lounge_topbar_title),
                popBackStack = popBackStack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { padding ->
        if (loungeState.loungeId == null){
            LoungeLoadingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
        else {
            LoungeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                loungeState = loungeState
            )
        }
    }
}

@Composable
private fun LoungeContent(
    modifier: Modifier,
    loungeState: LoungeState
){
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        LoungeMemberList(memberList = loungeState.memberList)
    }
}

@Composable
private fun LoungeMemberList(
    memberList: List<LoungeMember> = emptyList()
){
    val memberLimit = 6
    // 멤버 최대 6명
    // 4명이하 경우 -> memberList 보여주고 초대 서클, 빈 서클 추가
    // 5명인 경우 -> 초대 서클 추가
    // 6명인 경우 memberList 만
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ){
        items(memberList){ item ->
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                // 일단 그림자 효과 안둠
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.outline)
            ) {
                AsyncImage(
                    model = item.profileImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (memberList.size < memberLimit - 1) {
            item {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "invite",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        if (memberList.size < memberLimit){
            items(memberLimit - memberList.size - 1){
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .drawWithContent {
                            val stroke = Stroke(
                                width = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f)
                            )

                            drawContent()
                            drawCircle(
                                color = colorOutlineVariant,
                                radius = size.minDimension / 2f,
                                style = stroke
                            )
                        },
                    color = MaterialTheme.colorScheme.background
                ){}
            }
        }
    }
}


@Composable
private fun LoungeLoadingScreen(
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.lounge_create_loading),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoungeMemberListPreview(){
    LunchVoteTheme {
        LoungeMemberList(memberList = listOf(
            LoungeMember("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg", false)
        ))
    }
}


@Preview(showBackground = true)
@Composable
private fun LoungeScreenPreview(){
    LunchVoteTheme {
        LoungeScreen(
            loungeState = LoungeState(loungeId = "1234"),
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}