package kr.co.inbody.config.error

interface ChatError {

  data object InvalidChatType : Throwable() {
    private fun readResolve(): Any = InvalidChatType
    override val message: String = "유효하지 않은 채팅입니다."
  }
}