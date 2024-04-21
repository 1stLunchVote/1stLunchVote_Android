package com.jwd.lunchvote.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.jwd.lunchvote.presentation.ui.home.dialog.HomeJoinDialog
import com.jwd.lunchvote.presentation.ui.home.HomeRoute
import com.jwd.lunchvote.presentation.ui.login.LoginRoute
import com.jwd.lunchvote.presentation.ui.login.register.RegisterEmailRoute
import com.jwd.lunchvote.presentation.ui.lounge.LoungeRoute
import com.jwd.lunchvote.presentation.ui.lounge.member.LoungeMemberRoute
import com.jwd.lunchvote.presentation.ui.setting.SettingRoute
import com.jwd.lunchvote.presentation.ui.template.TemplateListRoute
import com.jwd.lunchvote.presentation.ui.template.add_template.AddTemplateRoute
import com.jwd.lunchvote.presentation.ui.template.dialog.TemplateListAddDialog
import com.jwd.lunchvote.presentation.ui.template.edit_template.EditTemplateRoute
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
    composable(LunchVoteNavRoute.Home) {
      HomeRoute(
        navigateToLounge = {
          navController.navigate(LunchVoteNavRoute.Lounge)
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
        navigateToTest = {
          // todo : 나중에 지우기
          navController.navigate(LunchVoteNavRoute.SecondVote)
        },
        navigateToFirstVote = {
          navController.navigate(LunchVoteNavRoute.FirstVote, "loungeId"/*TODO*/)
        },
        openJoinDialog = {
          navController.navigate(LunchVoteNavRoute.HomeJoinDialog)
        },
        showSnackBar = showSnackBar
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
        navigateToSecondVote = {
          navController.navigate(LunchVoteNavRoute.SecondVote.name)
        },
        openTemplateDialog = { navController.navigate(LunchVoteNavRoute.FirstVoteTemplateDialog) },
        openVoteExitDialog = { navController.navigate(LunchVoteNavRoute.VoteExitDialog) },
        showSnackBar = showSnackBar
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
        openAddDialog = { navController.navigate(LunchVoteNavRoute.TemplateListAddDialog) },
        popBackStack = { navController.popBackStack() },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.EditTemplate) {
      EditTemplateRoute(
        openDeleteDialog = { navController.navigate(LunchVoteNavRoute.EditTemplateDeleteDialog) },
        openConfirmDialog = { navController.navigate(LunchVoteNavRoute.EditTemplateConfirmDialog) },
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
    composable(LunchVoteNavRoute.Login) {
      LoginRoute(
        navigateToHome = { navController.navigateWithPop(LunchVoteNavRoute.Home) },
        navigateToRegisterEmail = { navController.navigate(LunchVoteNavRoute.RegisterEmail) },
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.RegisterEmail){
      RegisterEmailRoute(
        showSnackBar = showSnackBar
      )
    }
    composable(LunchVoteNavRoute.Setting) {
      SettingRoute(
        popBackStack = { navController.popBackStack() }
      )
    }

    dialog(LunchVoteNavRoute.HomeJoinDialog) {
      HomeJoinDialog(
        popBackStack = { navController.popBackStack() },
        navigateToLounge = { loungeId ->
          navController.navigate(LunchVoteNavRoute.Lounge, loungeId)
        },
        showSnackBar = showSnackBar
      )
    }
    dialog(LunchVoteNavRoute.TemplateListAddDialog) {
      TemplateListAddDialog(
        popBackStack = { navController.popBackStack() },
        navigateToAddTemplate = { templateName ->
          navController.navigate(LunchVoteNavRoute.AddTemplate, templateName)
        },
        showSnackBar = showSnackBar
      )
    }
    dialog(LunchVoteNavRoute.EditTemplateDeleteDialog) {

    }
    dialog(LunchVoteNavRoute.EditTemplateConfirmDialog) {

    }
  }
}