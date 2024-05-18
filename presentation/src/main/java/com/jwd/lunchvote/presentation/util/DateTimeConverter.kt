package com.jwd.lunchvote.presentation.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal fun LocalDateTime.toLong() = this.atZone(ZoneId.systemDefault()).toInstant().epochSecond

internal fun Long.toLocalDateTime() = LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())