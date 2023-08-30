package com.jwd.lunchvote.ui.login

import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.jwd.lunchvote.core.ui.base.ViewModelContract
import kotlinx.parcelize.Parcelize

class LoginContract {
    @Parcelize
    data class LoginState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    ) : ViewModelContract.State, Parcelable{
        override fun toParcelable(): Parcelable = this
    }

    sealed interface LoginEvent : ViewModelContract.Event {
        class SetEmail(val email: String) : LoginEvent
        class SetPwd(val pwd: String) : LoginEvent
        object OnClickEmailLogin : LoginEvent
        object OnClickGoogleLogin : LoginEvent
        object OnClickKakaoLogin : LoginEvent
        class ProcessGoogleLogin(val account: GoogleSignInAccount) : LoginEvent
        class ProcessKakaoLogin(val accessToken: String) : LoginEvent
        class OnLoginFailure(val canceled: Boolean = false) : LoginEvent

    }

    sealed interface LoginReduce : ViewModelContract.Reduce {
        class UpdateEmail(val email: String) : LoginReduce
        class UpdatePwd(val pwd: String) : LoginReduce
        class UpdateLoading(val isLoading: Boolean) : LoginReduce
    }

    sealed interface LoginSideEffect : ViewModelContract.SideEffect {
        object NavigateToHome : LoginSideEffect
        object LaunchGoogleLogin : LoginSideEffect
        object LaunchKakaoLogin : LoginSideEffect
        class ShowSnackBar(val message: String) : LoginSideEffect
    }

    sealed interface LoginDialogState: ViewModelContract.DialogState
}