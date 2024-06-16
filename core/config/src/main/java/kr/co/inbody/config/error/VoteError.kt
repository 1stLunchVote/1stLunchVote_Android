package kr.co.inbody.config.error

interface VoteError {

  data object NoVoteResult : Throwable() {
    private fun readResolve(): Any = NoVoteResult
    override val message: String = "투표 결과가 없습니다."
  }
}