package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Lounge
import kotlinx.coroutines.flow.Flow

interface LoungeRepository {

  suspend fun checkLoungeExistById(id: String): Boolean
  suspend fun createLounge(): String
  suspend fun getLoungeById(id: String): Lounge
  fun getLoungeFlowById(id: String): Flow<Lounge>
  fun getLoungeStatusFlowById(id: String): Flow<Lounge.Status>
  suspend fun joinLoungeById(id: String)
  suspend fun exitLoungeById(id: String)
  suspend fun quitLoungeById(id: String)
  suspend fun updateLoungeStatusById(id: String, status: Lounge.Status)
  suspend fun updateLounge(lounge: Lounge)
}