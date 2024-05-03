package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.SettingRepository
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
  private val userDataSource: UserDataSource
): SettingRepository {
  override suspend fun getUserById(id: String): User =
    userDataSource.getUserById(id).asDomain()

  override suspend fun updateUser(user: User) =
    userDataSource.updateUser(user.asData())
}