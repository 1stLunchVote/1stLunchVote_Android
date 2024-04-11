package com.jwd.lunchvote.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

enum class LunchVoteNavRoute(
  val args: List<Pair<String, NavType<*>>> = emptyList(),
  val links: List<String> = emptyList(),
) {
  Login,
  Home,
  LoungeMember,
  RegisterEmail,
  Profile,

  FirstVote,
  SecondVote,

  TemplateList,
  EditTemplate,
  AddTemplate,

  Setting,

  Lounge(args = listOf("id" to NavType.StringType)),
  Tips
}

internal val LunchVoteNavRoute.route: String
  get() = this.name + this.args.joinToString { "?${it.first}={${it.first}}" }

internal fun LunchVoteNavRoute.routeWithArgs(arguments: List<Any?>): String =
  this.name + this.args.zip(arguments) { key, value -> "?${key.first}=$value" }.joinToString()

internal val LunchVoteNavRoute.arguments: List<NamedNavArgument>
  get() = this.args.map { (key, navType) -> navArgument(key) { type = navType } }

internal val LunchVoteNavRoute.deepLinks: List<NavDeepLink>
  get() = this.links.map { navDeepLink { uriPattern = it } }