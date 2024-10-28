package kr.co.inbody.config.error

interface ContactError {

  object NoContact : Throwable() {
    private fun readResolve(): Any = NoContact
    override val message: String = "문의가 존재하지 않습니다."
  }

  data object DeletedContact : Throwable() {
    private fun readResolve(): Any = DeletedContact
    override val message: String = "삭제된 문의입니다."
  }

  data object ContactCategoryError : Throwable() {
    private fun readResolve(): Any = ContactCategoryError
    override val message: String = "유효하지 않은 카테고리입니다."
  }
}