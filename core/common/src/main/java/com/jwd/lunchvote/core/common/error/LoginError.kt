package com.jwd.lunchvote.core.common.error

interface LoginError {
  data object LoginFailure : Throwable() {
    private fun readResolve(): Any = LoginFailure
    override val message: String = "로그인에 실패했습니다."
  }

  data object LoginCanceled : Throwable() {
    private fun readResolve(): Any = LoginCanceled
    override val message: String = "로그인을 취소했습니다."
  }

  data object NoUser : Throwable() {
    private fun readResolve(): Any = NoUser
    override val message: String = "유저 정보를 확인할 수 없습니다. 다시 로그인해주세요."
  }

  data object NoEmail : Throwable() {
    private fun readResolve(): Any = NoUser
    override val message: String = "이메일을 확인할 수 없습니다."
  }

  data object TokenFailed : Throwable() {
    private fun readResolve(): Any = TokenFailed
    override val message: String = "토큰을 발급받지 못했습니다."
  }

  data object NoKakaoToken : Throwable() {
    private fun readResolve(): Any = NoKakaoToken
    override val message: String = "카카오 액세스 토큰을 확인할 수 없습니다. 다시 로그인해주세요."
  }

  data object CustomTokenFailed : Throwable() {
    private fun readResolve(): Any = CustomTokenFailed
    override val message: String = "커스텀 토큰을 발급받지 못했습니다."
  }
}