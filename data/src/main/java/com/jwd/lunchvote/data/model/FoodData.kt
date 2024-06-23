package com.jwd.lunchvote.data.model

data class FoodData(
  val id: String,
  val name: String,
  val image: ByteArray
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FoodData

    if (id != other.id) return false
    if (name != other.name) return false
    if (!image.contentEquals(other.image)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + image.contentHashCode()
    return result
  }
}