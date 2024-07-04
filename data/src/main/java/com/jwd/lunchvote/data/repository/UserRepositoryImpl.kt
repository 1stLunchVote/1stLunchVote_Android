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

  override suspend fun checkEmailExists(email: String): Boolean =
    userDataSource.checkEmailExists(email)

  override suspend fun checkNameExists(name: String): Boolean =
    userDataSource.checkNameExists(name)

  override suspend fun createUser(user: User): String =
    userDataSource.createUser(user.asData())

  override suspend fun getUserById(id: String): User =
    userDataSource.getUserById(id).asDomain()

  override suspend fun getUserByName(name: String): User =
    userDataSource.getUserByName(name).asDomain()

  override suspend fun updateUser(user: User) {
    userDataSource.updateUser(user.asData())
  }
}