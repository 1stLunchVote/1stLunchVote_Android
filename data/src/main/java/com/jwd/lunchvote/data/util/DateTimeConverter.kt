package com.jwd.lunchvote.data.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal fun ZonedDateTime.toLong() =
  this.toInstant().epochSecond

internal fun Long.toZonedDateTime() =
  ZonedDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.of("Asia/Seoul"))