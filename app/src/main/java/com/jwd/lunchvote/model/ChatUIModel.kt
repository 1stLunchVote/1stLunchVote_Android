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
    val profileImage: String?,
    // 0 = 전송 완료, 1 = 전송 중, 2 = 전송 실패
    val sendStatus: Int = 0
): Parcelable