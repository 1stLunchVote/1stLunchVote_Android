package kr.co.inbody.config.error

interface FoodError {

  data object LoadFailure : Throwable() {
    private fun readResolve(): Any = LoadFailure
    override val message: String = "음식 정보를 불러오는데 실패했습니다."
  }
}