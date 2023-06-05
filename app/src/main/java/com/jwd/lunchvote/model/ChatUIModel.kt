package com.jwd.lunchvote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatUIModel(
    val content: String,
    val messageType: Int,
    val isMine: Boolean,
    val sender: String,
    val createdAt: String,
    val profileImage: String?
): Parcelable