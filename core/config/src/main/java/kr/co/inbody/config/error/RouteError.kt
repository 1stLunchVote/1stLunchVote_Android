package kr.co.inbody.config.error

interface RouteError {

  data object NoArguments : Throwable() {
    private fun readResolve(): Any = NoArguments
    override val message: String = "유효하지 않은 요청입니다."
  }
}