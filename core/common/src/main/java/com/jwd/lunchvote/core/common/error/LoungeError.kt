package com.jwd.lunchvote.core.common.error

interface LoungeError {

  data object NoLounge : Throwable() {
    private fun readResolve(): Any = NoLounge
    override val message: String = "라운지 정보를 확인할 수 없습니다."
  }

  data object InvalidLoungeStatus : Throwable() {
    private fun readResolve(): Any = InvalidLoungeStatus
    override val message: String = "유효하지 않은 투표 방 상태입니다."
  }

  data object InvalidMemberStatus : Throwable() {
    private fun readResolve(): Any = InvalidMemberStatus
    override val message: String = "유효하지 않은 회원 상태입니다."
  }

  data object InvalidMember : Throwable() {
    private fun readResolve(): Any = InvalidMember
    override val message: String = "유효하지 않은 참가자입니다. 다시 시도해주세요."
  }
}