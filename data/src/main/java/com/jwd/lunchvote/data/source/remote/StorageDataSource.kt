package com.jwd.lunchvote.data.source.remote

interface StorageDataSource {

  suspend fun uploadProfileImage(userId: String, image: ByteArray): String
}