package com.jwd.lunchvote.model

import android.os.Parcelable
import com.jwd.lunchvote.domain.entity.FoodStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodUIModel (
    val foodId: Long,
    val imageUrl: String,
    val name: String,
    val status: FoodStatus = FoodStatus.DEFAULT
): Parcelable