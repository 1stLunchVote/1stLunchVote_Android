package com.jwd.lunchvote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SecondVoteTileUIModel(
    val foodImg: String?,
    val foodName: String
): Parcelable
