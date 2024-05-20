package com.jwd.lunchvote.presentation.model

enum class FoodStatus {
  DEFAULT, LIKE, DISLIKE;

  fun nextStatus(): FoodStatus = when (this) {
    DEFAULT -> LIKE
    LIKE -> DISLIKE
    DISLIKE -> DEFAULT
  }
}