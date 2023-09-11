package com.jwd.lunchvote.model.enums

enum class FoodStatus {
    DEFAULT,
    LIKE,
    DISLIKE;

    fun nextStatus(): FoodStatus =
        when (this) {
            DEFAULT -> LIKE
            LIKE -> DISLIKE
            DISLIKE -> DEFAULT
        }
}