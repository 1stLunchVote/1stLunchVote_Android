package com.jwd.lunchvote.presentation.model.type

enum class FoodStatusUIType {
  DEFAULT, LIKE, DISLIKE;

  fun nextStatus(): FoodStatusUIType = when (this) {
    DEFAULT -> LIKE
    LIKE -> DISLIKE
    DISLIKE -> DEFAULT
  }
}