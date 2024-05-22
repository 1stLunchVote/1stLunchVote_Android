package com.jwd.lunchvote.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

internal fun ZonedDateTime.toLong() = this.toEpochSecond()

internal fun Long.toZonedDateTime() = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.of("UTC"))