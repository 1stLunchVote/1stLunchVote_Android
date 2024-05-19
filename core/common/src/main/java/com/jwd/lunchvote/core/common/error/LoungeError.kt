package com.jwd.lunchvote.core.common.error

interface LoungeError {

  data object NoLounge : Throwable() {
    private fun readResolve(): Any = NoLounge
    override val message: String = "라운지 정보를 확인할 수 없습니다."
  }

  data object LoungeQuit : Throwable() {
    private fun readResolve(): Any = LoungeQuit
    override val message: String = "투표 방이 종료되었습니다."
  }

  data object LoungeStarted : Throwable() {
    private fun readResolve(): Any = LoungeStarted
    override val message: String = "이미 시작된 투표 방입니다."
  }

  data object LoungeFinished : Throwable() {
    private fun readResolve(): Any = LoungeFinished
    override val message: String = "이미 종료된 투표 방입니다."
  }

  data object InvalidLounge : Throwable() {
    private fun readResolve(): Any = InvalidLounge
    override val message: String = "유효하지 않은 투표 방입니다."
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

  data object NoOwner : Throwable() {
    private fun readResolve(): Any = NoOwner
    override val message: String = "투표 방이 종료되었습니다."
  }
}