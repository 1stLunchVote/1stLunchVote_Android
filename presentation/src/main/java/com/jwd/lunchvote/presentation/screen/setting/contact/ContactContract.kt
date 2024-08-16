package com.jwd.lunchvote.presentation.screen.setting.contact

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class ContactContract {
  @Parcelize
  data class ContactState(
    val text: String = ""
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface ContactEvent: ViewModelContract.Event {
    data object ScreenInitialize: ContactEvent

    data object OnClickBackButton: ContactEvent
  }

  sealed interface ContactReduce : ViewModelContract.Reduce {
    data class UpdateText(val text: String) : ContactReduce
  }

  sealed interface ContactSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack : ContactSideEffect
    data class ShowSnackbar(val message: UiText) : ContactSideEffect
  }
}