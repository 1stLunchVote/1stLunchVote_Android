package com.jwd.lunchvote.ui.home

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class HomeContract {
    @Parcelize
    data class HomeState(
        val isLoading: Boolean = false,
    ): ViewModelContract.State, Parcelable

    sealed class HomeEvent: ViewModelContract.Event {
    }

    sealed class HomeReduce : ViewModelContract.Reduce {
    }

    sealed class HomeSideEffect: ViewModelContract.SideEffect {
    }
}