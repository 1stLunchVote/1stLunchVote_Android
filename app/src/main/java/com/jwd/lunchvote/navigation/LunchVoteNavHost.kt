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
                    }
                )
            }

            composable(LunchVoteNavRoute.Lounge.name +"?id={id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) {
                LoungeRoute(
                    navigateToMember = {
                        navHostController.navigate(LunchVoteNavRoute.LoungeMember.name
                                + "?id=${it.uid},nickname=${it.nickname},profileUrl=${it.profileImage},isOwner=${it.isOwner}")
                    },
                    popBackStack = { navHostController.popBackStack() }
                )
            }

            composable(LunchVoteNavRoute.LoungeMember.name + "?id={id},nickname={nickname},profileUrl={profileUrl},isOwner={isOwner}",
                arguments = listOf(
                    navArgument("id") {
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
            ){
                LoungeMemberRoute(
                    popBackStack = { navHostController.popBackStack() }
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
                        navHostController.navigate(LunchVoteNavRoute.HomeNavigation.name){
                            popUpTo(0)
                        }
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

enum class LunchVoteNavRoute {
    LoginNavigation,
    HomeNavigation,

    Login,
    Home,
    Lounge,
    LoungeMember,
    RegisterEmail,
    Profile,
}
