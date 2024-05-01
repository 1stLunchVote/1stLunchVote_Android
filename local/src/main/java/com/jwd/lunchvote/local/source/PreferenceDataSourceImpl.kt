package com.jwd.lunchvote.local.source

import android.content.SharedPreferences
import com.jwd.lunchvote.data.source.local.PreferenceDataSource
import javax.inject.Inject

class PreferenceDataSourceImpl @Inject constructor(
  private val preference: SharedPreferences
) : PreferenceDataSource {

  companion object {
    const val EMAIL = "email"
  }

  override var email: String?
    get() = preference.getString(EMAIL, null)
    set(value) { preference.edit().putString(EMAIL, value).apply() }
}