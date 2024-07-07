package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.FriendData

interface FriendDataSource {

  suspend fun requestFriend(userId: String, friendId: String)
  suspend fun acceptFriend(id: String)
  suspend fun rejectFriend(id: String)

  suspend fun getSentFriendRequests(userId: String): List<FriendData>
  suspend fun getReceivedFriendRequests(userId: String): List<FriendData>

  suspend fun getFriends(userId: String): List<String>
  suspend fun deleteFriend(userId: String, friendId: String)
}