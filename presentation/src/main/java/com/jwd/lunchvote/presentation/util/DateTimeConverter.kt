package com.jwd.lunchvote.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal fun ZonedDateTime.toLong() =
  this.toInstant().epochSecond

internal fun Long.toZonedDateTime() =
  ZonedDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.of("Asia/Seoul"))

internal val INITIAL_DATE_TIME = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul"))
