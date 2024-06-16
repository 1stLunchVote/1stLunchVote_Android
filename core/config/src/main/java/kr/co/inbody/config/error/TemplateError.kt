package kr.co.inbody.config.error

interface TemplateError {

  data object NoTemplate : Throwable() {
    private fun readResolve(): Any = NoTemplate
    override val message: String = "템플릿을 찾을 수 없습니다."
  }

  data object DeletedTemplate : Throwable() {
    private fun readResolve(): Any = DeletedTemplate
    override val message: String = "템플릿이 삭제되었습니다."
  }
}