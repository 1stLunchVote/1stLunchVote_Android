package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
  val id: String = "",
  val name: String = "",
  val image: ByteArray = byteArrayOf()
): Parcelable {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FoodUIModel

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