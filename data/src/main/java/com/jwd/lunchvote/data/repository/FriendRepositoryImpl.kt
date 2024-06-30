package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.FriendDataSource
import com.jwd.lunchvote.domain.repository.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
  private val friendDataSource: FriendDataSource
): FriendRepository {


}