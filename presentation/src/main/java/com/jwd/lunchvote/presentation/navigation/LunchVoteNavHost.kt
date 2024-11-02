package com.jwd.lunchvote.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jwd.lunchvote.presentation.screen.friends.FriendListRoute
import com.jwd.lunchvote.presentation.screen.friends.request.FriendRequestRoute
import com.jwd.lunchvote.presentation.screen.home.HomeRoute
import com.jwd.lunchvote.presentation.screen.login.LoginRoute
import com.jwd.lunchvote.presentation.screen.login.register.email_verification.EmailVerificationRoute
import com.jwd.lunchvote.presentation.screen.login.register.nickname.NicknameRoute
import com.jwd.lunchvote.presentation.screen.login.register.password.PasswordRoute
import com.jwd.lunchvote.presentation.screen.lounge.LoungeRoute
import com.jwd.lunchvote.presentation.screen.lounge.member.LoungeMemberRoute
import com.jwd.lunchvote.presentation.screen.lounge.setting.LoungeSettingRoute
import com.jwd.lunchvote.presentation.screen.setting.SettingRoute
import com.jwd.lunchvote.presentation.screen.setting.contact.ContactRoute
import com.jwd.lunchvote.presentation.screen.setting.contact.contact_list.ContactListRoute
import com.jwd.lunchvote.presentation.screen.setting.contact.add_contact.AddContactRoute
import com.jwd.lunchvote.presentation.screen.setting.profile.ProfileRoute
import com.jwd.lunchvote.presentation.screen.template.TemplateListRoute
import com.jwd.lunchvote.presentation.screen.template.add_template.AddTemplateRoute
import com.jwd.lunchvote.presentation.screen.template.edit_template.EditTemplateRoute
import com.jwd.lunchvote.presentation.screen.tips.TipsRoute
import com.jwd.lunchvote.presentation.screen.vote.first.FirstVoteRoute
import com.jwd.lunchvote.presentation.screen.vote.result.VoteResultRoute
import com.jwd.lunchvote.presentation.screen.vote.second.SecondVoteRoute

@Composable
fun LunchVoteNavHost(
  navController: NavHostController,
  startDestination: String,
  modifier: Modifier = Modifier
) {
  fun NavHostController.navigateScreen(route: LunchVoteNavRoute, vararg arguments: Any?) {
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
        navigateToEmailVerification = { navController.navigateScreen(LunchVoteNavRoute.EmailVerification) }
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
          navController.navigateScreen(LunchVoteNavRoute.Lounge, loungeId)
        },
        navigateToTemplateList = { navController.navigateScreen(LunchVoteNavRoute.TemplateList) },
        navigateToFriendList = { navController.navigateScreen(LunchVoteNavRoute.FriendList) },
        navigateToSetting = { navController.navigateScreen(LunchVoteNavRoute.Setting) },
        navigateToTips = { navController.navigateScreen(LunchVoteNavRoute.Tips) }
      )
    }
    composable(LunchVoteNavRoute.Lounge) {
      LoungeRoute(
        popBackStack = { navController.popBackStack(LunchVoteNavRoute.Home) },
        navigateToLoungeSetting = { loungeId ->
          navController.navigateScreen(LunchVoteNavRoute.LoungeSetting, loungeId)
        },
        navigateToMember = { userId, loungeId ->
          navController.navigateScreen(LunchVoteNavRoute.LoungeMember, userId, loungeId)
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
          navController.navigateScreen(LunchVoteNavRoute.AddTemplate, templateName)
        },
        navigateToEditTemplate = { templateId ->
          navController.navigateScreen(LunchVoteNavRoute.EditTemplate, templateId)
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
         navigateToFriendRequest = { navController.navigateScreen(LunchVoteNavRoute.FriendRequest) },
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
        navigateToProfile = { navController.navigateScreen(LunchVoteNavRoute.Profile) },
        navigateToContactList = { navController.navigateScreen(LunchVoteNavRoute.ContactList) },
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) }
      )
    }
    composable(LunchVoteNavRoute.Profile) {
      ProfileRoute(
        popBackStack = { navController.popBackStack() },
        navigateToLogin = { navController.navigateWithPop(LunchVoteNavRoute.Login) }
      )
    }
    composable(LunchVoteNavRoute.ContactList) {
      ContactListRoute(
        popBackStack = { navController.popBackStack() },
        navigateToAddContact = { navController.navigateScreen(LunchVoteNavRoute.AddContact) },
        navigateToContact = { contactId ->
          navController.navigateScreen(LunchVoteNavRoute.Contact, contactId)
        }
      )
    }
    composable(LunchVoteNavRoute.Contact) {
      ContactRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
    composable(LunchVoteNavRoute.AddContact) {
      AddContactRoute(
        popBackStack = { navController.popBackStack() },
        navigateToContact = { contactId ->
          navController.navigateWithPop(LunchVoteNavRoute.Contact, contactId)
        }
      )
    }
    composable(LunchVoteNavRoute.Tips) {
      TipsRoute(
        popBackStack = { navController.popBackStack() }
      )
    }
  }
}