package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.UserData

interface UserDataSource {
  suspend fun createUser(user: UserData): String
}