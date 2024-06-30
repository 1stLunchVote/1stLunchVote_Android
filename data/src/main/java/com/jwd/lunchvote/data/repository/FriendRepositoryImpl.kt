package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.FriendDataSource
import com.jwd.lunchvote.domain.repository.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
  private val friendDataSource: FriendDataSource
): FriendRepository {

  override suspend fun requestFriend(userId: String, friendId: String) {
    friendDataSource.requestFriend(userId, friendId)
  }

  override suspend fun acceptFriend(id: String) {
    friendDataSource.acceptFriend(id)
  }

  override suspend fun rejectFriend(id: String) {
    friendDataSource.rejectFriend(id)
  }

  override suspend fun getSentFriendRequests(userId: String) =
    friendDataSource.getSentFriendRequests(userId).map { it.asDomain() }

  override suspend fun getReceivedFriendRequests(userId: String) =
    friendDataSource.getReceivedFriendRequests(userId).map { it.asDomain() }

  override suspend fun getFriends(userId: String) =
    friendDataSource.getFriends(userId)

  override suspend fun deleteFriend(userId: String, friendId: String) {
    friendDataSource.deleteFriend(userId, friendId)
  }
}