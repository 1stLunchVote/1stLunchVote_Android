package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class HomeContract {
    @Parcelize
    data class HomeState(
        val isLoading: Boolean = false,
    ): ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable = this
    }

    sealed interface HomeEvent: ViewModelContract.Event {
    }

    sealed interface HomeReduce : ViewModelContract.Reduce {
    }

    sealed interface HomeSideEffect: ViewModelContract.SideEffect {
        object NavigateToLounge : HomeSideEffect
        object NavigateToTemplate : HomeSideEffect
        object NavigateToSetting : HomeSideEffect
        object NavigateToTips : HomeSideEffect
        class ShowSnackBar(val message: String) : HomeSideEffect
    }
}