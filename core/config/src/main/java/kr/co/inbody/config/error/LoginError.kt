package kr.co.inbody.config.error

interface LoginError {

  data object LoginFailure : Throwable() {
    private fun readResolve(): Any = LoginFailure
    override val message: String = "로그인에 실패했습니다."
  }

  data object LoginCanceled : Throwable() {
    private fun readResolve(): Any = LoginCanceled
    override val message: String = "로그인을 취소했습니다."
  }

  data object NoEmail : Throwable() {
    private fun readResolve(): Any = NoEmail
    override val message: String = "이메일을 확인할 수 없습니다."
  }

  data object TokenFailed : Throwable() {
    private fun readResolve(): Any = TokenFailed
    override val message: String = "토큰을 발급받지 못했습니다."
  }
}