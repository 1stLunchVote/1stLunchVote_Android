package com.jwd.lunchvote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberUIModel(
    val uid: String,
    val profileImage: String?,
    val isReady: Boolean = false,
    val isOwner: Boolean = false
) : Parcelable
