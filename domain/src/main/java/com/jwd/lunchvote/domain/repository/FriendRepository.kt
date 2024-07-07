package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Friend

interface FriendRepository {

  suspend fun requestFriend(userId: String, friendId: String)
  suspend fun acceptFriend(id: String)
  suspend fun rejectFriend(id: String)

  suspend fun getSentFriendRequests(userId: String): List<Friend>
  suspend fun getReceivedFriendRequests(userId: String): List<Friend>

  suspend fun getFriends(userId: String): List<String>
  suspend fun deleteFriend(userId: String, friendId: String)
}