package com.jwd.lunchvote.data.source.remote

import com.jwd.lunchvote.data.model.LoungeData
import kotlinx.coroutines.flow.Flow

interface LoungeDataSource {

  suspend fun checkLoungeExistById(id: String): Boolean
  suspend fun getLoungeById(id: String): LoungeData
  fun getLoungeFlowById(id: String): Flow<LoungeData>
  fun getLoungeStatusFlowById(id: String): Flow<LoungeData.Status>
  suspend fun createLounge(): String
  suspend fun joinLoungeById(id: String): LoungeData
  suspend fun exitLoungeById(id: String)
  suspend fun quitLoungeById(id: String)
  suspend fun updateLoungeStatusById(id: String, status: LoungeData.Status)
  suspend fun updateLounge(lounge: LoungeData)
}