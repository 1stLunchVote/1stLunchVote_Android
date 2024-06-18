package kr.co.inbody.config.error

interface MemberError {

  data object InvalidMember : Throwable() {
    private fun readResolve(): Any = InvalidMember
    override val message: String = "유효하지 않은 참가자입니다. 다시 시도해주세요."
  }

  data object InvalidMemberType : Throwable() {
    private fun readResolve(): Any = InvalidMemberType
    override val message: String = "유효하지 않은 회원입니다."
  }

  data object InvalidMemberStatus : Throwable() {
    private fun readResolve(): Any = InvalidMemberStatus
    override val message: String = "유효하지 않은 회원 상태입니다."
  }
}