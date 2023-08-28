package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class HomeContract {
    @Parcelize
    data class HomeState(
        val isLoading: Boolean = false,
        val showJoinDialog: Boolean = false,
        val code: String = "KqND4zmJ59"
    ): ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable = this
    }

    sealed interface HomeEvent: ViewModelContract.Event {
        object OnClickLoungeButton : HomeEvent
        object OnClickJoinLoungeButton : HomeEvent
        class SetJoinCode(val code: String) : HomeEvent
        object OnClickDismissButtonOfJoinDialog : HomeEvent
        class OnClickConfirmButtonOfJoinDialog(val code: String) : HomeEvent
        object OnClickTemplateButton : HomeEvent
        object OnClickSettingButton : HomeEvent
        object OnClickTipsButton : HomeEvent
    }

    sealed interface HomeReduce : ViewModelContract.Reduce {
        object ShowJoinDialog : HomeReduce
        class UpdateJoinCode(val code: String) : HomeReduce
        object DismissJoinDialog : HomeReduce
        class ConfirmJoinDialog(val code: String) : HomeReduce
    }

    sealed interface HomeSideEffect: ViewModelContract.SideEffect {
        class NavigateToLounge(val loungeId: String?) : HomeSideEffect
        object NavigateToTemplateList : HomeSideEffect
        object NavigateToSetting : HomeSideEffect
        object NavigateToTips : HomeSideEffect
        class ShowSnackBar(val message: String) : HomeSideEffect
    }
}