package com.jwd.lunchvote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoungeMember(
    val profileImage: String?,
    val isReady: Boolean = false
) : Parcelable
