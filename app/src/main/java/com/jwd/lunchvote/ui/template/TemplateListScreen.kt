package com.jwd.lunchvote.ui.template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListSideEffect
import com.jwd.lunchvote.ui.template.TemplateListContract.TemplateListState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TemplateListRoute(
    navigateToEditTemplate: (String?) -> Unit,
    navigateToCreateTemplate: () -> Unit,
    popBackStack: () -> Unit,
    viewModel: TemplateListViewModel = hiltViewModel()
){
    val templateListState : TemplateListState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.sideEffect){
        viewModel.sideEffect.collectLatest {
            when(it){
                is TemplateListSideEffect.PopBaskStack -> popBackStack()
                is TemplateListSideEffect.NavigateToEditTemplate -> navigateToEditTemplate(it.templateId)
                is TemplateListSideEffect.NavigateToCreateTemplate -> navigateToCreateTemplate()
                is TemplateListSideEffect.ShowSnackBar -> snackBarHostState.showSnackbar(it.message)
            }
        }
    }

    TemplateListScreen(
        templateListState = templateListState,
        snackBarHostState = snackBarHostState,
    )
}

@Composable
private fun TemplateListScreen(
    templateListState: TemplateListState,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        if (templateListState.loading) {
            Dialog(onDismissRequest = {  }) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = CenterHorizontally
            ) {

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TemplateListScreenPreview() {
    LunchVoteTheme {
        TemplateListScreen(
            templateListState = TemplateListState(),
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}