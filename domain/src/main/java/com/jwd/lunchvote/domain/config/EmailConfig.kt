package com.jwd.lunchvote.domain.config

object EmailConfig {
  val REGEX = Regex("^([0-9a-zA-Z_\\.-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$")
  const val DYNAMIC_LINK = "https://lunchvote.page.link/6SuK"
}