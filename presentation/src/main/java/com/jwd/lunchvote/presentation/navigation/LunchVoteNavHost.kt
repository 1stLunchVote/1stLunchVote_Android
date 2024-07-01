package com.jwd.lunchvote.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jwd.lunchvote.presentation.ui.friends.FriendListRoute
import com.jwd.lunchvote.presentation.ui.home.HomeRoute
import com.jwd.lunchvote.presentation.ui.login.LoginRoute
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationRoute
import com.jwd.lunchvote.presentation.ui.login.register.nickname.NicknameRoute
import com.jwd.lunchvote.presentation.ui.login.register.password.PasswordRoute
import com.jwd.lunchvote.presentation.ui.lounge.LoungeRoute
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberRoute
import com.jwd.lunchvote.presentation.ui.setting.SettingRoute
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileRoute
import com.jwd.lunchvote.presentation.ui.template.TemplateListRoute
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateRoute
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateRoute
import com.jwd.lunchvote.presentation.ui.tips.TipsRoute
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteRoute
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultRoute
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteRoute

@Composable
fun LunchVoteNavHost(
  startDestination: String,
  navController: NavHostController,
  modifier: Modifier = Modifier
) {
  fun NavHostController.navigate(route: LunchVoteNavRoute, vararg arguments: Any?) {
    navigate(route.routeWithArgs(arguments.asList()))
  }

  fun NavHostController.navigateWithPop(route: LunchVoteNavRoute, vararg arguments: Any?) {
    navigate(route.routeWithArgs(arguments.asList())) { popBackStack() }
  }

  fun NavHostController.popBackStack(destinationRoute: LunchVoteNavRoute) {
    popBackStack(destinationRoute.name, false)
  }

  fun NavGraphBuilder.composable(
    route: LunchVoteNavRoute,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
  ) = composable(
    route = route.route,
    arguments = route.arguments,
    deepLinks = route.deepLinks,
    content = content
  )

  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier
  ) {
    composable(LunchVoteNavRoute.Login) {
      LoginRoute(
        navigateToHome = { navController.navigateWithPop(LunchVoteNavRoute.Home) },
        navigateToEmailVerification = { navController.navigate(LunchVoteNavRoute.EmailVerification) }
      )
    }
    composable(LunchVoteNavRoute.EmailVerification) {
      EmailVerificationRoute()
    }
    composable(LunchVoteNavRoute.Password) {
      PasswordRoute(
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) },
        navigateToNickname = { email, password ->
          navController.navigateWithPop(LunchVoteNavRoute.Nickname, email, password)
        }
      )
    }
    composable(LunchVoteNavRoute.Nickname) {
      NicknameRoute(
        navigateToHome = { navController.navigateWithPop(LunchVoteNavRoute.Home) }
      )
    }
    composable(LunchVoteNavRoute.Home) {
      HomeRoute(
        navigateToLounge = { loungeId ->
          navController.navigate(LunchVoteNavRoute.Lounge, loungeId)
        },
        navigateToTemplateList = { navController.navigate(LunchVoteNavRoute.TemplateList) },
        navigateToFriendList = { navController.navigate(LunchVoteNavRoute.FriendList) },
        navigateToSetting = { navController.navigate(LunchVoteNavRoute.Setting) },
        navigateToTips = { navController.navigate(LunchVoteNavRoute.Tips) }
      )
    }
    composable(LunchVoteNavRoute.Lounge) {
      LoungeRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToMember = { userId, loungeId ->
          navController.navigate(LunchVoteNavRoute.LoungeMember, userId, loungeId)
        },
        navigateToFirstVote = { loungeId ->
          navController.navigateWithPop(LunchVoteNavRoute.FirstVote, loungeId)
        }
      )
    }
    composable(LunchVoteNavRoute.LoungeMember) {
      LoungeMemberRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
    composable(LunchVoteNavRoute.FirstVote) {
      FirstVoteRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToSecondVote = { loungeId ->
          navController.navigateWithPop(LunchVoteNavRoute.SecondVote, loungeId)
        }
      )
    }
    composable(LunchVoteNavRoute.SecondVote) {
      SecondVoteRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToVoteResult = { loungeId ->
          navController.navigateWithPop(LunchVoteNavRoute.VoteResult, loungeId)
        }
      )
    }
    composable(LunchVoteNavRoute.VoteResult) {
      VoteResultRoute(
        navigateToHome = { navController.navigateWithPop(LunchVoteNavRoute.Home) }
      )
    }
    composable(LunchVoteNavRoute.TemplateList) {
      TemplateListRoute(
        navigateToAddTemplate = { templateName ->
          navController.navigate(LunchVoteNavRoute.AddTemplate, templateName)
        },
        navigateToEditTemplate = { templateId ->
          navController.navigate(LunchVoteNavRoute.EditTemplate, templateId)
        },
        popBackStack = { navController.popBackStack() }
      )
    }
    composable(LunchVoteNavRoute.EditTemplate) {
      EditTemplateRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
    composable(LunchVoteNavRoute.AddTemplate) {
      AddTemplateRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
    composable(LunchVoteNavRoute.FriendList) {
       FriendListRoute(
         popBackStack = { navController.popBackStack() },
         navigateToFriendRequest = { navController.navigate(LunchVoteNavRoute.FriendRequest) }
       )
    }
    composable(LunchVoteNavRoute.FriendRequest) {
      // FriendRequestRoute
    }
    composable(LunchVoteNavRoute.Setting) {
      SettingRoute(
        popBackStack = { navController.popBackStack() },
        navigateToProfile = { navController.navigate(LunchVoteNavRoute.Profile) },
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) }
      )
    }
    composable(LunchVoteNavRoute.Profile) {
      ProfileRoute(
        popBackStack = { navController.popBackStack() },
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) }
      )
    }
    composable(LunchVoteNavRoute.Tips) {
      TipsRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
  }
}