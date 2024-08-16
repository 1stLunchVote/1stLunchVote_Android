package kr.co.inbody.config.error

interface ContactError {

  data object ContactCategoryError : Throwable() {
    private fun readResolve(): Any = ContactCategoryError
    override val message: String = "유효하지 않은 카테고리입니다."
  }
}