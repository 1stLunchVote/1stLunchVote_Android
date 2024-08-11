package kr.co.inbody.config.error

interface ChatError {

  data object InvalidChatType : Throwable() {
    private fun readResolve(): Any = InvalidChatType
    override val message: String = "유효하지 않은 채팅입니다."
  }

  data object NoUser : Throwable() {
    private fun readResolve(): Any = NoUser
    override val message: String = "사용자 정보가 없습니다."
  }

  data object NoMessage : Throwable() {
    private fun readResolve(): Any = NoMessage
    override val message: String = "메세지가 없습니다."
  }
}