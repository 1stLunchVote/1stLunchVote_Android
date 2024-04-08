package com.jwd.lunchvote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jwd.lunchvote.presentation.ui.home.HomeRoute
import com.jwd.lunchvote.presentation.ui.login.LoginRoute
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailRoute
import com.jwd.lunchvote.presentation.ui.lounge.LoungeRoute
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberRoute
import com.jwd.lunchvote.presentation.ui.setting.SettingRoute
import com.jwd.lunchvote.presentation.ui.template.TemplateListRoute
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateRoute
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateRoute
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteRoute
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun LunchVoteNavHost(
    beforeLogin : Boolean,
    navHostController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val snackChannel = Channel<String>()

    NavHost(navController = navHostController,
        startDestination = if (beforeLogin) LunchVoteNavRoute.LoginNavigation.name else LunchVoteNavRoute.HomeNavigation.name
    ) {
        navigation(
            route = LunchVoteNavRoute.HomeNavigation.name,
            startDestination = LunchVoteNavRoute.Home.name
        ) {
            composable(LunchVoteNavRoute.Home.name) {
                HomeRoute(
                    navigateToLounge = { id ->
                        val query = if (id != null) "?id=$id" else ""
                        navHostController.navigate(LunchVoteNavRoute.Lounge.name + query)
                    },
                    navigateToTemplateList = {
                        navHostController.navigate(LunchVoteNavRoute.TemplateList.name)
                    },
                    navigateToSetting = {
                        navHostController.navigate(LunchVoteNavRoute.Setting.name)
                    },
                    navigateToTips = {
                        navHostController.navigate(LunchVoteNavRoute.Tips.name)
                    },
                    navigateToTest = {
                        // todo : 나중에 지우기
                        navHostController.navigate(LunchVoteNavRoute.SecondVote.name)
                    },
                    navigateToFirstVote = {
                        navHostController.navigate(LunchVoteNavRoute.FirstVote.name + "/loungeId"/*TODO*/)
                    },
                    messageFlow = snackChannel.receiveAsFlow()
                )
            }
            composable(LunchVoteNavRoute.Lounge.name + "?id={id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) {
                LoungeRoute(
                    navigateToMember = { m, loungeId, isOwner ->
                        navHostController.navigate(
                            LunchVoteNavRoute.LoungeMember.name
                                    + "?id=${m.uid},loungeId=${loungeId},nickname=${m.nickname},"
                                    + "profileUrl=${m.profileImage},isOwner=${isOwner}"
                        )
                    },
                    popBackStack = {
                        scope.launch { snackChannel.send(it) }
                        navHostController.popBackStack()
                    },
                    navigateToFirstVote = {
                        navHostController.navigate(LunchVoteNavRoute.FirstVote.name + "/loungeId=${it}",
                            navOptions = NavOptions.Builder().setPopUpTo(LunchVoteNavRoute.Home.name, true).build()
                        )
                    }
                )
            }

            composable(
                LunchVoteNavRoute.LoungeMember.name + "?id={id},loungeId={loungeId}," +
                        "nickname={nickname},profileUrl={profileUrl},isOwner={isOwner}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument("loungeId") {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument("nickname") {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument("profileUrl") {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument("isOwner") {
                        type = NavType.BoolType
                        nullable = false
                    },
                )
            ) {
                LoungeMemberRoute(
                    popBackStack = { navHostController.popBackStack() }
                )
            }

            composable(LunchVoteNavRoute.FirstVote.name + "/{loungeId}",
                arguments = listOf(
                    navArgument("loungeId") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) {
                FirstVoteRoute(
                    navigateToSecondVote = {
                        navHostController.navigate(LunchVoteNavRoute.SecondVote.name)
                    },
                    popBackStack = {
                        scope.launch { snackChannel.send(it) }

                        navHostController.navigate(LunchVoteNavRoute.Home.name) {
                            popUpTo(navHostController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(LunchVoteNavRoute.SecondVote.name){
                SecondVoteRoute(
                    popBackStack = {
                        scope.launch { snackChannel.send(it) }

                        navHostController.navigate(LunchVoteNavRoute.Home.name) {
                            popUpTo(navHostController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        navigation(
            route = LunchVoteNavRoute.TemplateNavigation.name,
            startDestination = LunchVoteNavRoute.TemplateList.name
        ) {
            composable(LunchVoteNavRoute.TemplateList.name) {
                TemplateListRoute(
                    navigateToEditTemplate = { templateId ->
                        navHostController.navigate(LunchVoteNavRoute.EditTemplate.name + "/${templateId}")
                    },
                    navigateToAddTemplate = { templateName ->
                        navHostController.navigate(LunchVoteNavRoute.AddTemplate.name + "/${templateName}")
                    },
                    popBackStack = { navHostController.popBackStack() },
                    savedStateHandle = it.savedStateHandle
                )
            }
            composable(
                LunchVoteNavRoute.EditTemplate.name + "/{templateId}",
                arguments = listOf(
                    navArgument("templateId") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) {
                EditTemplateRoute(
                    popBackStack = {
                        scope.launch { snackChannel.send(it) }
                        navHostController.popBackStack()
                    }
                )
            }
            composable(LunchVoteNavRoute.AddTemplate.name + "/{templateName}",
                arguments = listOf(
                    navArgument("templateName") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) {
                AddTemplateRoute(
                    popBackStack = {
                        scope.launch { snackChannel.send(it) }
                        navHostController.popBackStack()
                    }
                )
            }
        }

        navigation(
            route = LunchVoteNavRoute.LoginNavigation.name,
            startDestination = LunchVoteNavRoute.Login.name
        ) {
            composable(LunchVoteNavRoute.Login.name) {
                LoginRoute(
                    navigateToHome = {
                        navHostController.navigate(LunchVoteNavRoute.HomeNavigation.name)
                    },
                    navigateToRegisterEmail = {
                        navHostController.navigate(LunchVoteNavRoute.RegisterEmail.name)
                    }
                )
            }

            composable(LunchVoteNavRoute.RegisterEmail.name) {
                RegisterEmailRoute(

                )
            }
        }

        navigation(
            route = LunchVoteNavRoute.SettingNavigation.name,
            startDestination = LunchVoteNavRoute.Setting.name
        ) {
            composable(LunchVoteNavRoute.Setting.name) {
                SettingRoute(
                    popBackStack = {
                        navHostController.previousBackStackEntry?.savedStateHandle?.set(
                            SNACK_BAR_KEY,
                            it
                        )
                        navHostController.popBackStack()
                    }
                )
            }
        }
    }
}

const val SNACK_BAR_KEY = "message"

enum class LunchVoteNavRoute {
    LoginNavigation,
    HomeNavigation,
    TemplateNavigation,
    SettingNavigation,

    Login,
    Home,
    LoungeMember,
    RegisterEmail,
    Profile,

    FirstVote,
    SecondVote,

    TemplateList,
    EditTemplate,
    AddTemplate,

    Setting,

    Lounge,
    Tips
}
