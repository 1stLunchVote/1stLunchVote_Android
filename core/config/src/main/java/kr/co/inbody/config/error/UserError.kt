package kr.co.inbody.config.error

interface UserError {

  data object NoSession : Throwable() {
    private fun readResolve(): Any = NoSession
    override val message: String = "회원 정보를 확인할 수 없습니다. 다시 로그인해주세요."
  }

  data object NoUser : Throwable() {
    private fun readResolve(): Any = NoUser
    override val message: String = "회원 정보를 확인할 수 없습니다."
  }

  data object DeletedUser : Throwable() {
    private fun readResolve(): Any = DeletedUser
    override val message: String = "탈퇴한 회원입니다."
  }

  data object DuplicatedName: Throwable() {
    private fun readResolve(): Any = DuplicatedName
    override val message: String = "중복된 닉네임입니다."
  }
}