package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class HomeContract {
    @Parcelize
    data class HomeState(
        val isLoading: Boolean = false,
        val showJoinDialog: Boolean = false,
        val code: String = ""
    ): ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable = this
    }

    sealed interface HomeEvent: ViewModelContract.Event {
        object OnClickLoungeButton : HomeEvent
        object OnClickJoinLoungeButton : HomeEvent
        object OnClickDismissButtonOfJoinDialog : HomeEvent
        class SetJoinCode(val code: String) : HomeEvent
        object OnClickTemplateButton : HomeEvent
        object OnClickSettingButton : HomeEvent
        object OnClickTipsButton : HomeEvent
    }

    sealed interface HomeReduce : ViewModelContract.Reduce {
        object ShowJoinDialog : HomeReduce
        object DismissJoinDialog : HomeReduce
        class UpdateJoinCode(val code: String) : HomeReduce
    }

    sealed interface HomeSideEffect: ViewModelContract.SideEffect {
        object NavigateToLounge : HomeSideEffect
        object NavigateToTemplate : HomeSideEffect
        object NavigateToSetting : HomeSideEffect
        object NavigateToTips : HomeSideEffect
        class ShowSnackBar(val message: String) : HomeSideEffect
    }
}