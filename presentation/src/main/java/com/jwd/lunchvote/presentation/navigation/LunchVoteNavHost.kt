package com.jwd.lunchvote.presentation.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
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

  val snackChannel = Channel<String>()

  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier
  ) {
    composable(LunchVoteNavRoute.Home) {
      HomeRoute(
        navigateToLounge = { id ->
          val query = if (id != null) "?id=$id" else ""
          navController.navigate(LunchVoteNavRoute.Lounge.name + query)
        },
        navigateToTemplateList = {
          navController.navigate(LunchVoteNavRoute.TemplateList.name)
        },
        navigateToSetting = {
          navController.navigate(LunchVoteNavRoute.Setting.name)
        },
        navigateToTips = {
          navController.navigate(LunchVoteNavRoute.Tips.name)
        },
        navigateToTest = {
          // todo : 나중에 지우기
          navController.navigate(LunchVoteNavRoute.SecondVote.name)
        },
        navigateToFirstVote = {
          navController.navigate(LunchVoteNavRoute.FirstVote.name + "/loungeId"/*TODO*/)
        },
        messageFlow = snackChannel.receiveAsFlow()
      )
    }
    composable(LunchVoteNavRoute.Lounge) {
      LoungeRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToMember = { member, loungeId, isOwner ->
          navController.navigate(LunchVoteNavRoute.LoungeMember, 
            id, loungeId, member.uid, member.nickname, member.profileImage, isOwner)
        },
        navigateToFirstVote = { loungeId ->
          navController.navigate(LunchVoteNavRoute.FirstVote, loungeId)
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
        navigateToSecondVote = {
          navController.navigate(LunchVoteNavRoute.SecondVote.name)
        }
      )
    }
    composable(LunchVoteNavRoute.SecondVote) {
      SecondVoteRoute(
        popBackStack = {navController.popBackStack(LunchVoteNavRoute.Home) }
      )
    }
    composable(LunchVoteNavRoute.TemplateList) {
      TemplateListRoute(
        navigateToEditTemplate = { templateId ->
          navController.navigate(LunchVoteNavRoute.EditTemplate, templateId)
        },
        navigateToAddTemplate = { templateName ->
          navController.navigate(LunchVoteNavRoute.AddTemplate, templateName)
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
    composable(LunchVoteNavRoute.Login) {
      LoginRoute(
        navigateToHome = {
          navController.navigateWithPop(LunchVoteNavRoute.Home)
        },
        navigateToRegisterEmail = {
          navController.navigate(LunchVoteNavRoute.RegisterEmail)
        }
      )
    }
    composable(LunchVoteNavRoute.RegisterEmail){
      RegisterEmailRoute()
    }
    composable(LunchVoteNavRoute.Setting) {
      SettingRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
  }
}

const val SNACK_BAR_KEY = "message"