package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.local.PreferenceDataSource
import com.jwd.lunchvote.domain.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
  private val preferenceDataSource: PreferenceDataSource
): PreferenceRepository {

  override var email: String?
    get() = preferenceDataSource.email
    set(value) { preferenceDataSource.email = value }
}