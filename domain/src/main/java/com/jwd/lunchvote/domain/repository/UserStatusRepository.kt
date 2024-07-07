package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.UserStatus

interface UserStatusRepository {

  suspend fun createUserStatus(userId: String)

  suspend fun setUserOnline(userId: String)
  suspend fun setUserOffline(userId: String)
  suspend fun setUserLounge(userId: String, loungeId: String?)

  suspend fun getUserStatus(userId: String): UserStatus?
}