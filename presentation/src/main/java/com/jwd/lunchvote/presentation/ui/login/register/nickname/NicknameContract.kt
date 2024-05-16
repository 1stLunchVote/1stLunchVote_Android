package com.jwd.lunchvote.presentation.ui.login.register.nickname

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class NicknameContract {
  @Parcelize
  data class NicknameState(
    val nickname: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface NicknameEvent : ViewModelContract.Event {
    data class OnNicknameChange(val nickname: String) : NicknameEvent
    data object OnClickNextButton : NicknameEvent
  }

  sealed interface NicknameReduce : ViewModelContract.Reduce {
    data class UpdateNickname(val nickname: String) : NicknameReduce
  }

  sealed interface NicknameSideEffect : ViewModelContract.SideEffect {
    data object NavigateToHome : NicknameSideEffect
    data class ShowSnackBar(val message: UiText) : NicknameSideEffect
  }
}