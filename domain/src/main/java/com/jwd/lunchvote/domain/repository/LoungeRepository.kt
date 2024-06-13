package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.Lounge
import kotlinx.coroutines.flow.Flow

interface LoungeRepository {

  suspend fun checkLoungeExistById(id: String): Boolean
  suspend fun createLounge(): String
  fun getLoungeStatusFlowById(id: String): Flow<Lounge.Status>
  suspend fun getLoungeById(id: String): Lounge
  suspend fun joinLoungeById(id: String)
  suspend fun exitLoungeById(id: String)
  suspend fun quitLoungeById(id: String)
  suspend fun updateLoungeStatusById(id: String, status: Lounge.Status)
  suspend fun saveVoteResultById(id: String, electedFoodId: String)
  suspend fun getAllVoteResults(): List<String>
}