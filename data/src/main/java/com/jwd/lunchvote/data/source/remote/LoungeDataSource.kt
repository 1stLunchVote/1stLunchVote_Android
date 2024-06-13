package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.LoungeData
import kotlinx.coroutines.flow.Flow

interface LoungeDataSource {

  suspend fun checkLoungeExistById(id: String): Boolean
  suspend fun createLounge(): String
  fun getLoungeStatusFlowById(id: String): Flow<LoungeData.Status>
  suspend fun getLoungeById(id: String): LoungeData
  suspend fun joinLoungeById(id: String)
  suspend fun exitLoungeById(id: String)
  suspend fun quitLoungeById(id: String)
  suspend fun updateLoungeStatusById(id: String, status: LoungeData.Status)
  suspend fun saveVoteResultById(id: String, electedFoodId: String)
  suspend fun getAllVoteResults(): List<String>
}