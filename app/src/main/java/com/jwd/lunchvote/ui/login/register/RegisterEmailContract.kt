package com.jwd.lunchvote.ui.login.register

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class RegisterEmailContract {
    @Parcelize
    data class RegisterEmailState(
        val email: String = "",
    ) : ViewModelContract.State, Parcelable {
        override fun toParcelable(): Parcelable? = this
    }

    sealed interface RegisterEmailEvent : ViewModelContract.Event {
        class SetEmail(val email: String) : RegisterEmailEvent
        object OnClickConfirm : RegisterEmailEvent
    }

    sealed interface RegisterEmailReduce : ViewModelContract.Reduce {
        class UpdateEmail(val email: String) : RegisterEmailReduce
    }

    sealed interface RegisterEmailSideEffect : ViewModelContract.SideEffect {
        object NavigateToRegisterPassword : RegisterEmailSideEffect
        class ShowSnackBar(val message: String) : RegisterEmailSideEffect
    }
}