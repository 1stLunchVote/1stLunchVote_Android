package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.User

interface SettingRepository {
  suspend fun getUserById(id: String): User
  suspend fun updateUser(user: User)
  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}