package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.UserStatusDataSource
import com.jwd.lunchvote.domain.entity.UserStatus
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import javax.inject.Inject

class UserStatusRepositoryImpl @Inject constructor(
  private val userStatusDataSource: UserStatusDataSource
) : UserStatusRepository {

  override suspend fun createUserStatus(userId: String) {
    userStatusDataSource.createUserStatus(userId)
  }

  override suspend fun setUserOnline(userId: String) {
    userStatusDataSource.setUserOnline(userId)
  }

  override suspend fun setUserOffline(userId: String) {
    userStatusDataSource.setUserOffline(userId)
  }

  override suspend fun setUserLounge(userId: String, loungeId: String?) {
    userStatusDataSource.setUserLounge(userId, loungeId)
  }

  override suspend fun getUserStatus(userId: String): UserStatus? =
    userStatusDataSource.getUserStatus(userId)?.asDomain()
}