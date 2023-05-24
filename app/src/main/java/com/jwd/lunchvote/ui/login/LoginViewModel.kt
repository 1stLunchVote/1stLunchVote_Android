package com.jwd.lunchvote.ui.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.jwd.lunchvote.core.ui.base.BaseStateViewModel
import com.jwd.lunchvote.ui.login.LoginContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseStateViewModel<LoginState, LoginEvent, LoginReduce, LoginSideEffect>(savedStateHandle){
    override fun createInitialState(savedState: Parcelable?): LoginState {
        return savedState as? LoginState ?: LoginState()
    }

    override fun reduceState(state: LoginState, reduce: LoginReduce): LoginState {
        return when (reduce) {
            is LoginReduce.UpdateEmail -> {
                state.copy(email = reduce.email)
            }

            is LoginReduce.UpdatePwd -> {
                state.copy(password = reduce.pwd)
            }
        }
    }

    override fun handleEvents(event: LoginEvent) {
        when(event){
            is LoginEvent.SetEmail -> {
                updateState(LoginReduce.UpdateEmail(event.email))
            }
            is LoginEvent.SetPwd -> {
                updateState(LoginReduce.UpdatePwd(event.pwd))
            }
            is LoginEvent.OnClickLogin -> {
//                sendSideEffect(LoginSideEffect.NavigateToHome)
            }
        }
    }

}