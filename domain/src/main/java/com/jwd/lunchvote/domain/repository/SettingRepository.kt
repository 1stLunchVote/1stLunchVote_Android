package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.User

interface SettingRepository {
  suspend fun getUserById(id: String): User
}