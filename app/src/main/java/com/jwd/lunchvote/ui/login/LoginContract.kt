package com.jwd.lunchvote.ui.login

import android.os.Parcelable
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class LoginContract {
    @Parcelize
    data class LoginState(
        val email: String = "",
        val password: String = "",
    ) : ViewModelContract.State, Parcelable{
        override fun toParcelable(): Parcelable = this
    }

    sealed interface LoginEvent : ViewModelContract.Event {
        class SetEmail(val email: String) : LoginEvent
        class SetPwd(val pwd: String) : LoginEvent
        object OnClickLogin : LoginEvent
    }

    sealed interface LoginReduce : ViewModelContract.Reduce {
        class UpdateEmail(val email: String) : LoginReduce
        class UpdatePwd(val pwd: String) : LoginReduce
    }

    sealed interface LoginSideEffect : ViewModelContract.SideEffect {
        object NavigateToHome : LoginSideEffect
    }
}