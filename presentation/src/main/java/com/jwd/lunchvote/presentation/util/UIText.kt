package com.jwd.lunchvote.presentation.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kr.co.inbody.config.error.UnknownError

sealed class UiText{
  data class DynamicString(val value: String) : UiText()
  class StringResource(
    @StringRes val resId: Int,
    vararg val args: Any
  ) : UiText()
  class ErrorString(val error: Throwable) : UiText()

  @Composable
  fun asString() : String{
    return when (this) {
      is DynamicString -> value
      is StringResource -> stringResource(resId, *args)
      is ErrorString -> error.message ?: UnknownError.UNKNOWN
    }
  }

  fun asString(context: Context) : String{
    return when (this) {
      is DynamicString -> value
      is StringResource -> context.getString(resId, *args)
      is ErrorString -> error.message ?: UnknownError.UNKNOWN
    }
  }
}