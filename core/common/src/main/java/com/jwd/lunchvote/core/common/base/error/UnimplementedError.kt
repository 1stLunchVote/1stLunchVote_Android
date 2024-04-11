package com.jwd.lunchvote.core.common.base.error

data object UnimplementedError : Throwable() {
  private fun readResolve(): Any = UnimplementedError
  override val message: String = "Not Yet Implemented"
}