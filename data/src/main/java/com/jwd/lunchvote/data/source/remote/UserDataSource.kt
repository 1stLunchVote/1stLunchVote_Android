package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.UserData

interface UserDataSource {

  suspend fun checkEmailExists(email: String): Boolean
  suspend fun checkNameExists(name: String): Boolean
  suspend fun createUser(user: UserData): String
  suspend fun getUserById(id: String): UserData
  suspend fun updateUser(user: UserData)
}