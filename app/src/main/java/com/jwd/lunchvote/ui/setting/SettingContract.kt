package com.jwd.lunchvote.ui.setting

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class SettingContract {
  @Parcelize
  data class SettingState(
    val message: String = ""
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface SettingEvent: ViewModelContract.Event {
    data object OnClickBackButton: SettingEvent
  }

  sealed interface SettingReduce : ViewModelContract.Reduce {

  }

  sealed interface SettingSideEffect: ViewModelContract.SideEffect {
    data class PopBackStack(val message: String) : SettingSideEffect
    data class ShowSnackBar(val message: String) : SettingSideEffect
  }

  sealed interface SettingDialogState: ViewModelContract.DialogState
}