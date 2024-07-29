package com.jwd.lunchvote.presentation.ui.lounge.setting

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class LoungeSettingContract {
  @Parcelize
  data class LoungeSettingState(
    val text: String = ""
  ) : ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface LoungeSettingEvent : ViewModelContract.Event {
    data object OnClickBackButton : LoungeSettingEvent
  }

  sealed interface LoungeSettingReduce : ViewModelContract.Reduce {
    data class UpdateText(val text: String) : LoungeSettingReduce
  }

  sealed interface LoungeSettingSideEffect : ViewModelContract.SideEffect {
    data object PopBackStack : LoungeSettingSideEffect
    data class ShowSnackbar(val message: UiText) : LoungeSettingSideEffect
  }

  companion object {
    const val MEMBER_COUNT_DIALOG = "member_count_dialog"
  }
}