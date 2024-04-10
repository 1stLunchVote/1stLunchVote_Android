package com.jwd.lunchvote.presentation.ui.login

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
        data class SetEmail(val email: String) : LoginEvent
        data class SetPwd(val pwd: String) : LoginEvent
        data object OnClickEmailLogin : LoginEvent
        data object OnClickGoogleLogin : LoginEvent
        data object OnClickKakaoLogin : LoginEvent
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
        data object NavigateToHome : LoginSideEffect
        data object LaunchGoogleLogin : LoginSideEffect
        data object LaunchKakaoLogin : LoginSideEffect
        class ShowSnackBar(val message: String) : LoginSideEffect
    }

    sealed interface LoginDialogState: ViewModelContract.DialogState
}