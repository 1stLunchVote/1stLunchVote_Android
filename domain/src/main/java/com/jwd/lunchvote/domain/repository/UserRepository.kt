package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.User

interface UserRepository {

  suspend fun checkUserExists(email: String): Boolean
  suspend fun createUser(user: User): String
  suspend fun getUserById(id: String): User
  suspend fun updateUser(user: User)
}