package com.jwd.lunchvote.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
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
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteRoute

@Composable
fun LunchVoteNavHost(
  startDestination: String,
  showSnackBar: suspend (String) -> Unit,
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

  fun NavGraphBuilder.dialog(
    route: LunchVoteNavRoute,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable (NavBackStackEntry) -> Unit
  ) = dialog(
    route = route.route,
    arguments = route.arguments,
    deepLinks = route.deepLinks,
    dialogProperties = dialogProperties,
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
        navigateToEmailVerification = { navController.navigate(LunchVoteNavRoute.EmailVerification) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.EmailVerification) {
      EmailVerificationRoute(
        navigateToPassword = { navController.navigate(LunchVoteNavRoute.Password) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Password) {
      PasswordRoute(
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) },
        navigateToNickname = { email, password ->
          navController.navigateWithPop(LunchVoteNavRoute.Nickname, email, password)
        },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Nickname) {
      NicknameRoute(
        navigateToHome = { navController.navigateWithPop(LunchVoteNavRoute.Home) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Home) {
      HomeRoute(
        navigateToLounge = { loungeId ->
          navController.navigate(LunchVoteNavRoute.Lounge, loungeId)
        },
        navigateToTemplateList = {
          navController.navigate(LunchVoteNavRoute.TemplateList)
        },
        navigateToSetting = {
          navController.navigate(LunchVoteNavRoute.Setting)
        },
        navigateToTips = {
          navController.navigate(LunchVoteNavRoute.Tips)
        },
        showSnackBar = showSnackBar
      )

      // TODO : 나중에 지우기
      Row {
        Button(onClick = {
          navController.navigate(LunchVoteNavRoute.FirstVote, "loungeId")
        }) {
          Text(text = "1차 투표 테스트")
        }
        Button(onClick = { navController.navigate(LunchVoteNavRoute.SecondVote) }) {
          Text(text = "2차 투표 화면 테스트")
        }
      }
    }
    composable(LunchVoteNavRoute.Lounge) {
      LoungeRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToMember = { userId, loungeId ->
          navController.navigate(LunchVoteNavRoute.LoungeMember, userId, loungeId)
        },
        navigateToFirstVote = { loungeId ->
          navController.navigateWithPop(LunchVoteNavRoute.FirstVote, loungeId)
        },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.LoungeMember) {
      LoungeMemberRoute(
        popBackStack = { navController.popBackStack() },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.FirstVote) {
      FirstVoteRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToSecondVote = { navController.navigate(LunchVoteNavRoute.SecondVote.name) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.SecondVote) {
      SecondVoteRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) }
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
        popBackStack = { navController.popBackStack() },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.EditTemplate) {
      EditTemplateRoute(
        popBackStack = { navController.popBackStack() },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.AddTemplate) {
      AddTemplateRoute(
        popBackStack = { navController.popBackStack() },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Setting) {
      SettingRoute(
        popBackStack = { navController.popBackStack() },
        navigateToProfile = { navController.navigate(LunchVoteNavRoute.Profile) },
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Profile) {
      ProfileRoute(
        popBackStack = { navController.popBackStack() },
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Tips) {
      TipsRoute(
        popBackStack = { navController.popBackStack() },
        showSnackBar = showSnackBar
      )
    }
  }
}