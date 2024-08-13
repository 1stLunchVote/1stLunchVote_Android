package com.jwd.lunchvote.remote.util

import com.google.firebase.Timestamp

internal fun Timestamp.toLong() =
  this.seconds

internal fun Long.toTimestamp() =
  Timestamp(this, 0)