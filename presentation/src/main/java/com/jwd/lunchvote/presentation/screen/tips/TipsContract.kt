package com.jwd.lunchvote.presentation.screen.tips

import android.os.Parcelable
import com.jwd.lunchvote.presentation.base.ViewModelContract
import com.jwd.lunchvote.presentation.util.UiText
import kotlinx.parcelize.Parcelize

class TipsContract {
  @Parcelize
  data class TipsState(
    val tabIndex: Int = 0
  ): ViewModelContract.State, Parcelable {
    override fun toParcelable(): Parcelable = this
  }

  sealed interface TipsEvent: ViewModelContract.Event {
    data object OnClickBackButton: TipsEvent
    data class OnClickTab(val tabIndex: Int): TipsEvent
  }

  sealed interface TipsReduce : ViewModelContract.Reduce {
    data class UpdateTabIndex(val tabIndex: Int): TipsReduce
  }

  sealed interface TipsSideEffect: ViewModelContract.SideEffect {
    data object PopBackStack : TipsSideEffect
    data class ShowSnackbar(val message: UiText) : TipsSideEffect
  }
}