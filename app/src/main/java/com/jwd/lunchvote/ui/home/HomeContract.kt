package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class HomeContract {
    @Parcelize
    data class HomeState(
        val isLoading: Boolean = false,
    ): ViewModelContract.State, Parcelable

    sealed interface HomeEvent: ViewModelContract.Event {
        object OnCreateLounge: HomeEvent
    }

    sealed interface HomeReduce : ViewModelContract.Reduce {
    }

    sealed interface HomeSideEffect: ViewModelContract.SideEffect {
        data class NavigateToLounge(val loungeId: String?): HomeSideEffect
    }
}