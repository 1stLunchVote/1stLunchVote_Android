package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.mapper.asDomain
import com.jwd.lunchvote.data.source.remote.LoungeDataSource
import com.jwd.lunchvote.domain.entity.Lounge
import com.jwd.lunchvote.domain.repository.LoungeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoungeRepositoryImpl @Inject constructor(
  private val loungeDataSource: LoungeDataSource
) : LoungeRepository {

  override suspend fun checkLoungeExistById(id: String): Boolean =
    loungeDataSource.checkLoungeExistById(id)

  override suspend fun createLounge(): String =
    loungeDataSource.createLounge()

  override suspend fun getLoungeById(id: String): Lounge =
    loungeDataSource.getLoungeById(id).asDomain()

  override fun getLoungeFlowById(id: String): Flow<Lounge> =
    loungeDataSource.getLoungeFlowById(id).map { it.asDomain() }

  override fun getLoungeStatusFlowById(id: String): Flow<Lounge.Status> =
    loungeDataSource.getLoungeStatusFlowById(id).map { it.asDomain() }

  override suspend fun joinLoungeById(id: String) {
    loungeDataSource.joinLoungeById(id)
  }

  override suspend fun exitLoungeById(id: String) {
    loungeDataSource.exitLoungeById(id)
  }

  override suspend fun quitLoungeById(id: String) {
    loungeDataSource.quitLoungeById(id)
  }

  override suspend fun updateLoungeStatusById(id: String, status: Lounge.Status) {
    loungeDataSource.updateLoungeStatusById(id, status.asData())
  }

  override suspend fun updateLounge(lounge: Lounge) {
    loungeDataSource.updateLounge(lounge.asData())
  }
}