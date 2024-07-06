package kr.co.inbody.config.error

interface LoungeError {

  data object NoLounge : Throwable() {
    private fun readResolve(): Any = LoungeQuit
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

  data object CreateLoungeFailed : Throwable() {
    private fun readResolve(): Any = CreateLoungeFailed
    override val message: String = "투표 방 생성에 실패했습니다."
  }

  data object ExiledMember : Throwable() {
    private fun readResolve(): Any = ExiledMember
    override val message: String = "추방되어 방 참여가 불가능합니다."
  }

  data object JoinLoungeFailed : Throwable() {
    private fun readResolve(): Any = JoinLoungeFailed
    override val message: String = "투표 방 참가에 실패했습니다."
  }

  data object InvalidLoungeStatus : Throwable() {
    private fun readResolve(): Any = InvalidLoungeStatus
    override val message: String = "유효하지 않은 투표 방 상태입니다."
  }

  data object NoOwner : Throwable() {
    private fun readResolve(): Any = NoOwner
    override val message: String = "투표 방이 종료되었습니다."
  }

  data object NoMember : Throwable() {
    private fun readResolve(): Any = NoMember
    override val message: String = "참가자가 없습니다."
  }

  data object FullMember : Throwable() {
    private fun readResolve(): Any = FullMember
    override val message: String = "참가자가 꽉 찼습니다."
  }
}