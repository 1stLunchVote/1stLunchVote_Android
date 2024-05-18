package com.jwd.lunchvote.domain.repository

interface StorageRepository {

  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}