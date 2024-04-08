package com.jwd.lunchvote.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberUIModel(
    val uid: String,
    val nickname: String,
    val profileImage: String?,
    val isReady: Boolean = false,
    val isOwner: Boolean = false,
    val isMine: Boolean = false
) : Parcelable
