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
        data object OnClickLoungeButton : HomeEvent
        data object OnClickJoinLoungeButton : HomeEvent
        data class SetJoinCode(val code: String) : HomeEvent
        data object OnClickDismissButtonOfJoinDialog : HomeEvent
        data class OnClickConfirmButtonOfJoinDialog(val code: String) : HomeEvent
        data object OnClickTemplateButton : HomeEvent
        data object OnClickSettingButton : HomeEvent
        data object OnClickTipsButton : HomeEvent
    }

    sealed interface HomeReduce : ViewModelContract.Reduce {
        data object ShowJoinDialog : HomeReduce
        data class UpdateJoinCode(val code: String) : HomeReduce
        data object DismissJoinDialog : HomeReduce
        data class ConfirmJoinDialog(val code: String) : HomeReduce
    }

    sealed interface HomeSideEffect: ViewModelContract.SideEffect {
        data class NavigateToLounge(val loungeId: String?) : HomeSideEffect
        data object NavigateToTemplateList : HomeSideEffect
        data object NavigateToSetting : HomeSideEffect
        data object NavigateToTips : HomeSideEffect
        data class ShowSnackBar(val message: String) : HomeSideEffect
    }

    sealed interface HomeDialogState: ViewModelContract.DialogState
}