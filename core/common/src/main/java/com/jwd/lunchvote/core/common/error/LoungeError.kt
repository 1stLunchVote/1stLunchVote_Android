package com.jwd.lunchvote.core.common.error

interface LoungeError {

  data object NoLounge : Throwable() {
    private fun readResolve(): Any = NoLounge
    override val message: String = "라운지 정보를 확인할 수 없습니다."
  }
}