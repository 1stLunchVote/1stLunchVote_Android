package com.jwd.lunchvote.remote.model

data class LoungeRemote(
  val status: String = STATUS_CREATED,
  val members: Int = 0
) {

  companion object {
    const val STATUS_CREATED = "created"
    const val STATUS_QUIT = "quit"
    const val STATUS_FIRST_VOTE = "first_vote"
    const val STATUS_SECOND_VOTE = "second_vote"
    const val STATUS_FINISHED = "finished"
  }
}
