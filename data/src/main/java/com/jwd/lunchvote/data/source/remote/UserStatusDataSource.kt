package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.UserStatusData

interface UserStatusDataSource {

  suspend fun createUserStatus(userId: String)

  suspend fun setUserOnline(userId: String)
  suspend fun setUserOffline(userId: String)
  suspend fun setUserLounge(userId: String, loungeId: String?)

  suspend fun getUserStatus(userId: String): UserStatusData?
}