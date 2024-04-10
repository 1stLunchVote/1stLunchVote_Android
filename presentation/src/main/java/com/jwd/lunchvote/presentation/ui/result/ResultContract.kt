package com.jwd.lunchvote.presentation.ui.result

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class ResultContract {
    @Parcelize
    data class ResultState(
        val image: String = "",
        val name: String = ""
    ): ViewModelContract.State, Parcelable

    sealed interface ResultEvent : ViewModelContract.Event{

    }

    sealed interface ResultReduce: ViewModelContract.Reduce{

    }

    sealed interface ResultSideEffect: ViewModelContract.SideEffect{

    }

    sealed interface ResultDialogState: ViewModelContract.DialogState{

    }
}