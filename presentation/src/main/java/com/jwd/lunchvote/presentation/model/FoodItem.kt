package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItem(
  val food: FoodUIModel,
  val status: Status
) : Parcelable {

  enum class Status {
    DEFAULT, LIKE, DISLIKE;
  }

  fun nextStatus(): FoodItem =
    FoodItem(
      food = food,
      status = when(status){
        Status.DEFAULT -> Status.LIKE
        Status.LIKE -> Status.DISLIKE
        Status.DISLIKE -> Status.DEFAULT
      }
    )
}