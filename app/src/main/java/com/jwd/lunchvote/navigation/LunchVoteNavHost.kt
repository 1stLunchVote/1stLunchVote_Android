package com.jwd.lunchvote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.jwd.lunchvote.ui.home.HomeRoute
import com.jwd.lunchvote.ui.login.LoginRoute
import com.jwd.lunchvote.ui.login.register.RegisterEmailRoute

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
                    navigateToLounge = {
                        navHostController.navigate(LunchVoteNavRoute.Lounge.name)
                    },
                    navigateToTemplate = {
                        navHostController.navigate(LunchVoteNavRoute.Template.name)
                    },
                    navigateToSetting = {
                        navHostController.navigate(LunchVoteNavRoute.Setting.name)
                    },
                    navigateToTips = {
                        navHostController.navigate(LunchVoteNavRoute.Tips.name)
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

    }
}

enum class LunchVoteNavRoute {
    LoginNavigation,
    HomeNavigation,

    Login,
    Home,
    RegisterEmail,
    Profile,

    Lounge,
    Template,
    Setting,
    Tips
}
