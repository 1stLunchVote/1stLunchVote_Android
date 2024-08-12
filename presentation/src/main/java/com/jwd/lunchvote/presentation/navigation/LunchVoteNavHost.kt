package com.jwd.lunchvote.presentation.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.ui.MainActivity
import com.jwd.lunchvote.presentation.ui.friends.FriendListRoute
import com.jwd.lunchvote.presentation.ui.friends.request.FriendRequestRoute
import com.jwd.lunchvote.presentation.ui.home.HomeRoute
import com.jwd.lunchvote.presentation.ui.login.LoginRoute
import com.jwd.lunchvote.presentation.ui.login.register.email_verification.EmailVerificationRoute
import com.jwd.lunchvote.presentation.ui.login.register.nickname.NicknameRoute
import com.jwd.lunchvote.presentation.ui.login.register.password.PasswordRoute
import com.jwd.lunchvote.presentation.ui.lounge.LoungeRoute
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberRoute
import com.jwd.lunchvote.presentation.ui.lounge.setting.LoungeSettingRoute
import com.jwd.lunchvote.presentation.ui.setting.SettingRoute
import com.jwd.lunchvote.presentation.ui.setting.profile.ProfileRoute
import com.jwd.lunchvote.presentation.ui.template.TemplateListRoute
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateRoute
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateRoute
import com.jwd.lunchvote.presentation.ui.tips.TipsRoute
import com.jwd.lunchvote.presentation.ui.vote.first.FirstVoteRoute
import com.jwd.lunchvote.presentation.ui.vote.result.VoteResultRoute
import com.jwd.lunchvote.presentation.ui.vote.second.SecondVoteRoute
import com.jwd.lunchvote.presentation.util.ConnectionManager
import com.jwd.lunchvote.presentation.widget.LunchVoteDialog

@Composable
fun LunchVoteNavHost(
  startDestination: String,
  connectionManager: ConnectionManager,
  navController: NavHostController,
  modifier: Modifier = Modifier,
  context: Context = LocalContext.current
) {
  fun NavHostController.checkNetwork() =
    if (connectionManager.currentState == ConnectionManager.LOST) 
      navigate(LunchVoteNavRoute.NetworkLostDialog.route)
    else
      Unit
  
  fun NavHostController.navigate(route: LunchVoteNavRoute, vararg arguments: Any?) {
    navigate(route.routeWithArgs(arguments.asList()))
    checkNetwork()
  }

  fun NavHostController.navigateWithPop(route: LunchVoteNavRoute, vararg arguments: Any?) {
    navigate(route.routeWithArgs(arguments.asList())) { popBackStack() }
    checkNetwork()
  }

  fun NavHostController.popBackStack(destinationRoute: LunchVoteNavRoute) {
    popBackStack(destinationRoute.name, false)
    checkNetwork()
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

  val connectionState by connectionManager.connectionState.collectAsStateWithLifecycle()
  LaunchedEffect(connectionState) {
    navController.checkNetwork()
  }

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
        navigateToLoungeSetting = { loungeId ->
          navController.navigate(LunchVoteNavRoute.LoungeSetting, loungeId)
        },
        navigateToMember = { userId, loungeId ->
          navController.navigate(LunchVoteNavRoute.LoungeMember, userId, loungeId)
        },
        navigateToFirstVote = { loungeId ->
          navController.navigateWithPop(LunchVoteNavRoute.FirstVote, loungeId)
        }
      )
    }
    composable(LunchVoteNavRoute.LoungeSetting) {
      LoungeSettingRoute(
        popBackStack = { navController.popBackStack() }
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
         navigateToFriendRequest = { navController.navigate(LunchVoteNavRoute.FriendRequest) },
         navigateToLounge = { friendId ->
           navController.navigateWithPop(LunchVoteNavRoute.Lounge, friendId)
         }
       )
    }
    composable(LunchVoteNavRoute.FriendRequest) {
      FriendRequestRoute(
        popBackStack = { navController.popBackStack() }
      )
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

    dialog(
      route = LunchVoteNavRoute.NetworkLostDialog,
      dialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
      )
    ) {
      LunchVoteDialog(
        title = stringResource(R.string.network_lost_dialog_title),
        dismissText = stringResource(R.string.network_lost_dialog_dismiss),
        onDismissRequest = { (context as MainActivity).finish() },
        confirmText = stringResource(R.string.network_lost_dialog_confirm),
        onConfirmation = {
          navController.popBackStack()
          navController.checkNetwork()
        }
      ) {
        Text(
          text = stringResource(R.string.network_lost_dialog_body)
        )
      }
    }
  }
}