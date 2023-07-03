package com.jwd.lunchvote.domain.entity

data class Food (
    val foodId: Long,
    val imageUrl: String,
    val name: String,
    val status: FoodStatus
)