package com.jwd.lunchvote.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

enum class LunchVoteNavRoute(
  val arguments: List<NamedNavArgument> = emptyList(),
  val links: List<String> = emptyList(),
) {
  Login,
  EmailVerification,
  Password,
  Nickname(
    arguments = listOf(
      navArgument("email") {
        type = NavType.StringType
      },
      navArgument("password") {
        type = NavType.StringType
      }
    )
  ),

  Home,
  LoungeMember,

  FirstVote,
  SecondVote,

  TemplateList,
  EditTemplate(
    arguments = listOf(
      navArgument("id") {
        type = NavType.StringType
      }
    )
  ),
  AddTemplate(
    arguments = listOf(
      navArgument("name") {
        type = NavType.StringType
      }
    )
  ),

  Setting,
  Profile,

  Lounge(
    arguments = listOf(
      navArgument("id") {
        type = NavType.StringType
        nullable = true
      }
    )
  ),
  Tips,

  HomeJoinDialog,
  TemplateListAddDialog,
  VoteExitDialog,
  FirstVoteTemplateDialog,
}

internal val LunchVoteNavRoute.route: String
  get() = this.name + this.arguments.joinToString { "?${it.name}={${it.name}}" }

internal fun LunchVoteNavRoute.routeWithArgs(arguments: List<Any?>): String =
  this.name + this.arguments.zip(arguments) { key, value -> "?${key.name}=$value" }.joinToString()

internal val LunchVoteNavRoute.deepLinks: List<NavDeepLink>
  get() = this.links.map { navDeepLink { uriPattern = it } }