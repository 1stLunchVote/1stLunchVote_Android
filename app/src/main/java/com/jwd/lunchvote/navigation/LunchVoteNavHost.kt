package com.jwd.lunchvote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jwd.lunchvote.ui.home.HomeRoute
import com.jwd.lunchvote.ui.login.LoginRoute
import com.jwd.lunchvote.ui.login.register.RegisterEmailRoute
import com.jwd.lunchvote.ui.lounge.LoungeRoute
import com.jwd.lunchvote.ui.lounge.member.LoungeMemberRoute
import com.jwd.lunchvote.ui.vote.first.FirstVoteRoute
import com.jwd.lunchvote.ui.vote.second.SecondVoteRoute

@Composable
fun LunchVoteNavHost(
    beforeLogin : Boolean
) {
    val navHostController = rememberNavController()

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
                    navigateToTemplate = {
                        navHostController.navigate(LunchVoteNavRoute.Template.name)
                    },
                    navigateToSetting = {
                        navHostController.navigate(LunchVoteNavRoute.Setting.name)
                    },
                    navigateToTips = {
                        navHostController.navigate(LunchVoteNavRoute.Tips.name)
                    },
                    navigateToTest = {
                        // todo : 나중에 지우기
                        navHostController.navigate(LunchVoteNavRoute.VoteNavigation.name)
                    },
                    navigateToFirstVote = {
                        navHostController.navigate(LunchVoteNavRoute.FirstVote.name)
                    },
                    messageFlow = it.savedStateHandle.getStateFlow(SNACK_BAR_KEY, "")
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
                        navHostController.previousBackStackEntry?.savedStateHandle?.set(
                            SNACK_BAR_KEY,
                            it
                        )
                        navHostController.popBackStack()
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
        }

        navigation(
            route = LunchVoteNavRoute.VoteNavigation.name,
            startDestination = LunchVoteNavRoute.SecondVote.name
        ){
            composable(LunchVoteNavRoute.FirstVote.name) {
                FirstVoteRoute(
                    navigateToSecondVote = {
                        navHostController.navigate(LunchVoteNavRoute.SecondVote.name)
                    },
                    popBackStack = { navHostController.popBackStack() }
                )
            }
            composable(LunchVoteNavRoute.SecondVote.name){
                SecondVoteRoute(

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

    }
}

private const val SNACK_BAR_KEY = "message"

enum class LunchVoteNavRoute {
    LoginNavigation,
    HomeNavigation,
    VoteNavigation,

    Login,
    Home,
    LoungeMember,
    RegisterEmail,
    Profile,

    FirstVote,
    SecondVote,

    Lounge,
    Template,
    Setting,
    Tips
}
