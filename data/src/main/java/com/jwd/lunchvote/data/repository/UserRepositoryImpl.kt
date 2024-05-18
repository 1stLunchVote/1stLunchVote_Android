package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
  private val userDataSource: UserDataSource
): UserRepository {

  override suspend fun checkUserExists(email: String): Boolean =
    userDataSource.checkUserExists(email)

  override suspend fun createUser(user: User): String =
    userDataSource.createUser(user.asData())

  override suspend fun getUserById(id: String): User =
    userDataSource.getUserById(id).asDomain()

  override suspend fun updateUser(user: User) {
    userDataSource.updateUser(user.asData())
  }
}